package database

import (
	"database/sql"
	"fmt"
	"log"
	"sync"

	_ "github.com/go-sql-driver/mysql"
)

const (
	username = "root"
	password = ""
	hostname = "127.0.0.1:3306"
	dbname   = "budget_db"
)

var (
	dbInstance *sql.DB
	dbOnce     sync.Once
)

// InitDB инициализирует соединение с базой данных
func InitDB() (*sql.DB, error) {
	var initErr error
	dbOnce.Do(func() {
		// Формирование строки подключения
		dsn := fmt.Sprintf("%s:%s@tcp(%s)/%s", username, password, hostname, dbname)

		// Открытие соединения с базой данных
		db, err := sql.Open("mysql", dsn)
		if err != nil {
			initErr = err
			return
		}

		// Проверка соединения
		err = db.Ping()
		if err != nil {
			initErr = err
			return
		}

		dbInstance = db
		log.Println("Успешное подключение к базе данных")
	})

	return dbInstance, initErr
}

// GetDB возвращает экземпляр соединения с базой данных
func GetDB() *sql.DB {
	if dbInstance == nil {
		log.Panic("База данных не инициализирована")
	}
	return dbInstance
}
