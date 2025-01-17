# API Documentation

## Overview
Это документация для API. Если есть пакетный менеджер npm для Node.js, тогда можной перейти в папку frontend и ввести: 

`npm install`

`npm start` 

Код в master

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
```

```
curl -X POST http://localhost:8000/register \
-H "Content-Type: application/json" \
-d '{"body": {"password": "your_password"}}'
```

### 2. Авторизация (/auth)

- **Метод:** POST
- **URL:** `http://localhost:8000/auth`
- **Заголовки:**
  - `Content-Type: application/json`
- **Тело запроса:**
```json
{
    "body": {
        "uuid": "your_uuid",
        "password": "your_password"
    }
}
```

```
curl -X POST http://localhost:8000/auth \
-H "Content-Type: application/json" \
-d '{"body": {"uuid": "your_uuid", "password": "your_password"}}'
```

### 3. Создание ссылки (/shorten)

- **Метод:** POST
- **URL:** `http://localhost:8000/shorten`
- **Заголовки:**
  - `Content-Type: application/json`
- **Тело запроса:**
```json
{
    "body": {
        "uuid": "your_uuid",
        "url": "http://example.com",
        "maxClicks": 10,
        "expirationTime": 60
    }
}
```

```
curl -X POST http://localhost:8000/shorten \
-H "Content-Type: application/json" \
-d '{"body": {"uuid": "your_uuid", "url": "http://example.com", "maxClicks": 10, "expirationTime": 60}}'
```

### 4. Получение ссылок (/links)

- **Метод:** POST
- **URL:** `http://localhost:8000/links`
- **Заголовки:**
  - `Content-Type: application/json`
- **Тело запроса:**
```json
{
    {
      "uuid": "your_uuid"
    }
}
```

```
curl -X POST http://localhost:8000/links \
-H "Content-Type: application/json" \
-d '{"uuid": "your_uuid"}'
```

### 5. Удаление ссылки (/links/delete)

- **Метод:** DELETE
- **URL:** `http://localhost:8000/links/delete/{id}`
- **Заголовки:**
  - `Content-Type: application/json`
- **Тело запроса:**

```
curl -X DELETE http://localhost:8000/links/delete/1
```

### 6. Редирект (/)

- **Метод:** GET
- **URL:** `http://localhost:8000/{shortUrl}`
- **Заголовки:**
  - `Content-Type: application/json`
- **Тело запроса:**

```
curl -L http://localhost:8000/shortUrl
```
