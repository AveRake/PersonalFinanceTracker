package models

import "time"

type BudgetItem struct {
	ID          int       `json:"id"`
	UserID      int       `json:"user_id"`
	Date        time.Time `json:"date"`
	Name        string    `json:"name"`
	Category    string    `json:"category"` // "income" или "expense"
	Amount      float64   `json:"amount"`
	Description string    `json:"description,omitempty"`
}

type BudgetRequest struct {
	Date        string  `json:"date"`
	Name        string  `json:"name"`
	Category    string  `json:"category"`
	Amount      float64 `json:"amount"`
	Description string  `json:"description,omitempty"`
}

type BudgetResponse struct {
	Date        string  `json:"date"`
	Name        string  `json:"name"`
	Category    string  `json:"category"`
	Amount      float64 `json:"amount"`
	Description string  `json:"description,omitempty"` 
}
