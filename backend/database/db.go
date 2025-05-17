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

func InitDB() (*sql.DB, error) {
	var initErr error
	dbOnce.Do(func() {
		dsn := fmt.Sprintf("%s:%s@tcp(%s)/%s", username, password, hostname, dbname)

		db, err := sql.Open("mysql", dsn)
		if err != nil {
			initErr = err
			return
		}

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

func GetDB() *sql.DB {
	if dbInstance == nil {
		log.Panic("База данных не инициализирована")
	}
	return dbInstance
}
