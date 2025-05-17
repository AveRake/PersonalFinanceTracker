package main

import (
	"budget-api/database"
	"budget-api/handlers"
	"budget-api/middleware"
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

func main() {
	if _, err := database.InitDB(); err != nil {
		log.Fatal("Database initialization failed:", err)
	}

	r := mux.NewRouter()

	r.HandleFunc("/register", handlers.Register).Methods("POST")
	r.HandleFunc("/login", handlers.Login).Methods("POST")
	r.HandleFunc("/reset-password", handlers.ResetPassword).Methods("POST")

	api := r.PathPrefix("/api").Subrouter()
	api.Use(middleware.JwtMiddleware)

	api.HandleFunc("/budget", handlers.AddBudgetItem).Methods("POST")
	api.HandleFunc("/budget/day/{date}", handlers.GetBudgetByDay).Methods("GET")
	api.HandleFunc("/budget/period/{start}/{end}", handlers.GetBudgetByPeriod).Methods("GET")
	api.HandleFunc("/change-password", handlers.ChangePassword).Methods("POST")

	log.Println("Server started on :8000")
	log.Fatal(http.ListenAndServe(":8000", r))
}
