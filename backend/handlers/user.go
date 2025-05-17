package handlers

import (
	"budget-api/database"
	"budget-api/middleware"
	"budget-api/models"
	"encoding/json"
	"net/http"

	"golang.org/x/crypto/bcrypt"
)

func ChangePassword(w http.ResponseWriter, r *http.Request) {
	userID, err := middleware.GetUserIDFromContext(r)
	if err != nil {
		http.Error(w, "Неавторизованный доступ", http.StatusUnauthorized)
		return
	}

	var req models.PasswordChangeRequest
	err = json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	db := database.GetDB()
	var currentPassword string
	err = db.QueryRow("SELECT password FROM users WHERE id = ?", userID).Scan(&currentPassword)
	if err != nil {
		http.Error(w, "Пользователь не найден", http.StatusNotFound)
		return
	}

	err = bcrypt.CompareHashAndPassword([]byte(currentPassword), []byte(req.OldPassword))
	if err != nil {
		http.Error(w, "Неверный старый пароль", http.StatusUnauthorized)
		return
	}

	newHashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.NewPassword), bcrypt.DefaultCost)
	if err != nil {
		http.Error(w, "Ошибка при изменении пароля", http.StatusInternalServerError)
		return
	}

	_, err = db.Exec("UPDATE users SET password = ? WHERE id = ?", string(newHashedPassword), userID)
	if err != nil {
		http.Error(w, "Ошибка при изменении пароля", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(map[string]string{"message": "Пароль успешно изменен"})
}
