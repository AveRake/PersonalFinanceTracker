const express = require('express');
const bodyParser = require('body-parser');
const mysql = require('mysql2');
const cors = require('cors');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const app = express();
const PORT = 3000;

app.use(cors());
app.use(bodyParser.json());

// Настройка подключения к базе данных
const db = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '',
  database: 'budget_app'
});

db.connect(err => {
  if (err) {
    console.error('Ошибка подключения к базе данных:', err);
    return;
  }
  console.log('Подключено к MySQL базе данных');
});

const JWT_SECRET = 'your_jwt_secret_key';

// Middleware для проверки JWT токена
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  
  if (!token) return res.sendStatus(401);
  
  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) return res.sendStatus(403);
    req.user = user;
    next();
  });
}

// Регистрация пользователя
app.post('/register', async (req, res) => {
  try {
    const { username, password, email } = req.body;
    
    // Проверка на существующего пользователя
    db.query('SELECT * FROM users WHERE username = ?', [username], async (err, results) => {
      if (err) throw err;
      
      if (results.length > 0) {
        return res.status(400).json({ message: 'Пользователь уже существует' });
      }
      
      // Хеширование пароля
      const hashedPassword = await bcrypt.hash(password, 10);
      
      // Создание пользователя
      db.query(
        'INSERT INTO users (username, password, email) VALUES (?, ?, ?)',
        [username, hashedPassword, email],
        (err, results) => {
          if (err) throw err;
          res.status(201).json({ message: 'Пользователь успешно зарегистрирован' });
        }
      );
    });
  } catch (error) {
    res.status(500).json({ message: 'Ошибка сервера' });
  }
});

// Вход пользователя
app.post('/login', (req, res) => {
  const { username, password } = req.body;
  
  db.query('SELECT * FROM users WHERE username = ?', [username], async (err, results) => {
    if (err) throw err;
    
    if (results.length === 0) {
      return res.status(401).json({ message: 'Неверное имя пользователя или пароль' });
    }
    
    const user = results[0];
    const passwordMatch = await bcrypt.compare(password, user.password);
    
    if (!passwordMatch) {
      return res.status(401).json({ message: 'Неверное имя пользователя или пароль' });
    }
    
    // Создание JWT токена
    const token = jwt.sign({ id: user.id, username: user.username }, JWT_SECRET, { expiresIn: '1h' });
    res.json({ token, userId: user.id });
  });
});

// Получение данных бюджета пользователя
app.get('/budget', authenticateToken, (req, res) => {
  const userId = req.user.id;
  
  db.query('SELECT * FROM budget_data WHERE user_id = ?', [userId], (err, results) => {
    if (err) throw err;
    res.json(results);
  });
});

// Добавление новой записи бюджета
app.post('/budget', authenticateToken, (req, res) => {
  const userId = req.user.id;
  const { category, amount, description, date } = req.body;
  
  db.query(
    'INSERT INTO budget_data (user_id, category, amount, description, date) VALUES (?, ?, ?, ?, ?)',
    [userId, category, amount, description, date],
    (err, results) => {
      if (err) throw err;
      res.status(201).json({ message: 'Запись успешно добавлена' });
    }
  );
});

app.listen(PORT, () => {
  console.log(`Сервер запущен на порту ${PORT}`);
});
