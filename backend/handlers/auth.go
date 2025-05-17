package handlers

import (
	"budget-api/database"
	"budget-api/models"
	"database/sql"
	"encoding/json"
	"errors"
	"net/http"
	"strings"
	"time"

	"github.com/dgrijalva/jwt-go"
	"golang.org/x/crypto/bcrypt"
)

var jwtKey = []byte("my_secret_key")

func Register(w http.ResponseWriter, r *http.Request) {
	var req models.RegisterRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// Хеширование пароля
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		http.Error(w, "Failed to hash password", http.StatusInternalServerError)
		return
	}

	// Сохранение пользователя
	db := database.GetDB()
	_, err = db.Exec(
		"INSERT INTO users (username, email, password) VALUES (?, ?, ?)",
		req.Username,
		req.Email,
		string(hashedPassword),
	)

	if err != nil {
		if strings.Contains(err.Error(), "Duplicate entry") {
			http.Error(w, "Username or email already exists", http.StatusConflict)
		} else {
			http.Error(w, "Failed to create user", http.StatusInternalServerError)
		}
		return
	}

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]string{"message": "User registered successfully"})
}

func Login(w http.ResponseWriter, r *http.Request) {
	var req models.LoginRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// Поиск пользователя
	db := database.GetDB()
	var user models.User
	err := db.QueryRow(
		"SELECT id, username, password FROM users WHERE username = ?",
		req.Username,
	).Scan(&user.ID, &user.Username, &user.Password)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			http.Error(w, "Invalid credentials", http.StatusUnauthorized)
		} else {
			http.Error(w, "Database error", http.StatusInternalServerError)
		}
		return
	}

	// Проверка пароля
	if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password)); err != nil {
		http.Error(w, "Invalid credentials", http.StatusUnauthorized)
		return
	}

	// Генерация токена
	expirationTime := time.Now().Add(24 * time.Hour)
	claims := &models.JwtClaims{
		UserID:   user.ID,
		Username: user.Username,
		StandardClaims: jwt.StandardClaims{
			ExpiresAt: expirationTime.Unix(),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString(jwtKey)
	if err != nil {
		http.Error(w, "Failed to generate token", http.StatusInternalServerError)
		return
	}

	// Ответ
	json.NewEncoder(w).Encode(map[string]interface{}{
		"token":      tokenString,
		"expires_at": expirationTime.Unix(),
		"user_id":    user.ID,
	})
}

func ResetPassword(w http.ResponseWriter, r *http.Request) {
	var req models.PasswordResetRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	// В реальном приложении здесь была бы логика сброса пароля
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]string{
		"message": "Password reset instructions sent if email exists",
	})
}
