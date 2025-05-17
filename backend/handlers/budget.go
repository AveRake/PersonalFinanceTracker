package handlers

import (
	"budget-api/database"
	"budget-api/middleware"
	"budget-api/models"
	"database/sql"
	"encoding/json"
	"log"
	"net/http"
	"time"

	"github.com/gorilla/mux"
)

func AddBudgetItem(w http.ResponseWriter, r *http.Request) {
	userID, err := middleware.GetUserIDFromContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}

	var req models.BudgetRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	if req.Category != "income" && req.Category != "expense" {
		http.Error(w, "Invalid category", http.StatusBadRequest)
		return
	}

	date, err := time.Parse("2006-01-02", req.Date)
	if err != nil {
		http.Error(w, "Invalid date format", http.StatusBadRequest)
		return
	}

	db := database.GetDB()
	_, err = db.Exec(
		"INSERT INTO budget_items (user_id, date, name, category, amount, description) VALUES (?, ?, ?, ?, ?, ?)",
		userID, date.Format("2006-01-02"), req.Name, req.Category, req.Amount, req.Description,
	)

	if err != nil {
		http.Error(w, "Failed to save budget item", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(map[string]string{"message": "Budget item added"})
}

func GetBudgetByDay(w http.ResponseWriter, r *http.Request) {
	log.Println("=== Начало обработки запроса ===")

	userID, err := middleware.GetUserIDFromContext(r)
	if err != nil {
		log.Printf("Ошибка авторизации: %v\n", err)
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	log.Printf("UserID: %d\n", userID)

	date := mux.Vars(r)["date"]
	log.Printf("Запрошенная дата: %s\n", date)

	db := database.GetDB()
	log.Println("Подключение к БД установлено")

	query := `
        SELECT 
            date, name, category, 
            amount, description 
        FROM budget_items 
        WHERE user_id = ? AND date = ?
    `
	log.Printf("Выполняем запрос:\n%s\nПараметры: %d, %s", query, userID, date)

	rows, err := db.Query(query, userID, date)
	if err != nil {
		log.Printf("Ошибка SQL-запроса: %v\n", err)
		http.Error(w, "Database error", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var items []models.BudgetResponse
	for rows.Next() {
		var dbDateStr string
		var item models.BudgetResponse
		var description sql.NullString

		if err := rows.Scan(&dbDateStr, &item.Name, &item.Category, &item.Amount, &description); err != nil {
			log.Printf("Ошибка чтения строки: %v\n", err)
			http.Error(w, "Data processing error", http.StatusInternalServerError)
			return
		}

		parsedDate, err := time.Parse("2006-01-02", dbDateStr)
		if err != nil {
			log.Printf("Ошибка парсинга даты: %v\n", err)
			http.Error(w, "Invalid date in database", http.StatusInternalServerError)
			return
		}
		item.Date = parsedDate.Format("2006-01-02")
		if description.Valid {
			item.Description = description.String
		} else {
			item.Description = ""
		}
		items = append(items, item)
	}

	log.Printf("Найдено записей: %d\n", len(items))
	json.NewEncoder(w).Encode(items)
	log.Println("=== Обработка завершена успешно ===")
}

func GetBudgetByPeriod(w http.ResponseWriter, r *http.Request) {
	userID, err := middleware.GetUserIDFromContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}

	vars := mux.Vars(r)
	startDate := vars["start"]
	endDate := vars["end"]

	if _, err := time.Parse("2006-01-02", startDate); err != nil {
		http.Error(w, "Invalid start date format", http.StatusBadRequest)
		return
	}
	if _, err := time.Parse("2006-01-02", endDate); err != nil {
		http.Error(w, "Invalid end date format", http.StatusBadRequest)
		return
	}

	db := database.GetDB()
	query := `
        SELECT 
            date, name, category, 
            amount, description 
        FROM budget_items 
        WHERE user_id = ? AND date BETWEEN ? AND ?
    `
	rows, err := db.Query(query, userID, startDate, endDate)
	if err != nil {
		http.Error(w, "Database error", http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var items []models.BudgetResponse
	for rows.Next() {
		var dbDateStr string
		var item models.BudgetResponse
		var description sql.NullString

		if err := rows.Scan(&dbDateStr, &item.Name, &item.Category, &item.Amount, &description); err != nil {
			http.Error(w, "Data processing error", http.StatusInternalServerError)
			return
		}

		parsedDate, err := time.Parse("2006-01-02", dbDateStr)
		if err != nil {
			http.Error(w, "Invalid date in database", http.StatusInternalServerError)
			return
		}
		item.Date = parsedDate.Format("2006-01-02")

		if description.Valid {
			item.Description = description.String
		} else {
			item.Description = ""
		}
		items = append(items, item)
	}

	json.NewEncoder(w).Encode(items)
}
