# API Documentation

## Overview
Это документация для API, предоставляющего функционал регистрации, авторизации и управления короткими ссылками.

## Эндпоинты

### 1. Регистрация (/register)

- **Метод:** POST
- **URL:** `http://localhost:8000/register`
- **Заголовки:**
  - `Content-Type: application/json`
- **Тело запроса:**
```json
{
    "body": {
        "password": "your_password"
    }
}
