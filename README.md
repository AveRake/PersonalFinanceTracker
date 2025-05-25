# Budget App - Personal Finance Manager

A modern mobile app for managing personal finances with secure cloud synchronization. Built with Node.js backend and Android (Jetpack Compose) client.

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
