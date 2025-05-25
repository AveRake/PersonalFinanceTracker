# Budget App - Personal Finance Manager

A modern mobile app for managing personal finances with secure cloud synchronization. Built with Node.js backend and Android (Jetpack Compose) client.

<p align="center">
  <img height="600em" alt="screenshot_login" src="https://github.com/user-attachments/assets/86aada49-5705-44e4-a67d-1c12fc931534" />
  <img height="600em" alt="screenshot_main" src="https://github.com/user-attachments/assets/6bea63a0-b2d4-4d22-aa85-17d73a70c962" />
</p>

## Key Features

- 💳 Expense tracking by categories
- 🔐 JWT authentication
- 📊 Financial insights and statistics
- ☁️ Cloud sync across devices
- 📱 Clean Material Design 3 UI

## 💻 Technology Stack

### Backend

- **Node.js** (v18+)x
- **Express** - web framework
- **MySQL** - database
- **JWT** - authentication
- **XAMPP** - local server

### Android Client

- **Kotlin**
- **Jetpack Compose**
- **Retrofit**
- **DataStore**
- **ViewModel** + **Coroutines**

## 🛠️ Installation & Setup

### Backend Setup

```bash
# Clone repository
git clone https://github.com/your-username/budget-app.git
cd budget-app/server

# Install dependencies
npm init -y
npm install express body-parser mysql2 cors bcrypt jsonwebtoken

# Start server
node server.js
