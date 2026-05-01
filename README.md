# Bank Card Management — REST API

Backend-приложение на Spring Boot для управления банковскими картами с JWT-аутентификацией, ролевым доступом и Swagger-документацией.

---

## Быстрый запуск

Требуется только **Docker** и **Docker Compose**.

```bash
docker-compose up --build
```

Приложение поднимется на [http://localhost:8080](http://localhost:8080).  
PostgreSQL запустится автоматически, Liquibase применит все миграции.

> При первом запуске Maven скачает зависимости — это займёт 2–3 минуты.  
> Последующие запуски используют Docker layer cache и проходят быстрее.

---

## Доступ после запуска

| Ресурс | URL |
|--------|-----|
| Главная страница | http://localhost:8080 |
| Панель администратора | http://localhost:8080/admin.html |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### Учётные данные по умолчанию

| Роль | Логин | Пароль |
|------|-------|--------|
| ADMIN | admin | admin1 |

Пользователей можно регистрировать через форму на главной странице.

---

## Стек технологий

| Технология | Назначение |
|-----------|-----------|
| Java 17 | Язык |
| Spring Boot 3.3.5 | Фреймворк |
| Spring Security 6 + JWT (JJWT 0.12.6) | Аутентификация и авторизация |
| Spring Data JPA + Hibernate | ORM |
| PostgreSQL 16 | База данных |
| Liquibase | Миграции схемы БД |
| MapStruct | Маппинг Entity ↔ DTO |
| SpringDoc OpenAPI 2.6 | Swagger UI |
| Docker + Docker Compose | Контейнеризация |
| JUnit 5 + Mockito + MockMvc | Тесты |

---

## Архитектура

```
src/main/java/com/example/bankcards/
├── config/          # SecurityConfig, OpenApiConfig
├── controller/      # REST-контроллеры
│   ├── AdminCardController   # /api/admin/cards/**
│   ├── CardController        # /api/cards/**
│   ├── AuthController        # /api/auth/**
│   └── PageController        # статические страницы
├── dto/
│   ├── Requests/    # LoginRequest, RegisterRequest, TransferRequest, ...
│   └── Responses/   # CardResponse, LoginResponse, TransferResponse, ...
├── entity/          # User, Card, CardRequest + Enums
├── exception/       # глобальный обработчик ошибок
├── mapper/          # CardMapper (MapStruct)
├── repository/      # UserRepository, CardRepository, CardRequestRepository
├── security/        # JwtUtil, JwtAuthenticationFilter
└── service/         # AuthServiceImpl, CardServiceImpl
```

Миграции БД: `src/main/resources/db/changelog/`

---

## API

### Аутентификация — `/api/auth`

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/auth/login` | Вход, возвращает JWT |
| POST | `/api/auth/register` | Регистрация нового пользователя |

### Карты пользователя — `/api/cards` (роль USER)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/api/cards` | Список своих карт (фильтр по статусу, пагинация) |
| GET | `/api/cards/{id}/balance` | Баланс карты |
| POST | `/api/cards/{id}/request-block` | Запрос на блокировку |
| POST | `/api/cards/transfer` | Перевод между своими картами по ID |
| POST | `/api/cards/transfer/phone` | Перевод по номеру телефона получателя |
| POST | `/api/cards/request` | Запрос на выпуск новой карты |

### Администрирование — `/api/admin` (роль ADMIN)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/api/admin/users` | Список всех пользователей |
| DELETE | `/api/admin/users/{id}` | Удалить пользователя |
| GET | `/api/admin/cards` | Все карты (фильтр по статусу и пользователю) |
| POST | `/api/admin/cards` | Создать карту |
| POST | `/api/admin/cards/{id}/block` | Заблокировать карту |
| POST | `/api/admin/cards/{id}/activate` | Активировать карту |
| DELETE | `/api/admin/cards/{id}` | Удалить карту |
| GET | `/api/admin/cards/requests` | Заявки на выпуск карт |
| POST | `/api/admin/cards/requests/{id}/approve` | Одобрить заявку |
| POST | `/api/admin/cards/requests/{id}/reject` | Отклонить заявку |

---

## Безопасность

- JWT-токен передаётся в заголовке `Authorization: Bearer <token>`
- Номер карты хранится в зашифрованном виде, отображается маской: `**** **** **** 1234`
- Пользователь имеет доступ только к своим картам — проверка по `userId` из токена
- Пароли хранятся в BCrypt-хэше

---

## Тесты

```bash
mvn test
```

Покрыты юнит-тестами:
- `CardServiceImplTest` — бизнес-логика карт и переводов
- `AuthServiceImplTest` — регистрация и аутентификация
- `CardControllerTest`, `AdminCardControllerTest`, `AuthControllerTest`, `PageControllerTest` — REST-слой через MockMvc

Тесты используют H2 in-memory БД — PostgreSQL для их запуска не нужен.

---

## Локальный запуск без Docker

Требуется: Java 17+, Maven 3.9+, PostgreSQL 16.

1. Создать БД и пользователя:
```sql
CREATE DATABASE bank_db;
CREATE USER bankuser WITH PASSWORD 'secret';
GRANT ALL PRIVILEGES ON DATABASE bank_db TO bankuser;
```

2. Запустить приложение:
```bash
mvn spring-boot:run
```

---

## Переменные окружения

Переопределяются через env-переменные или `application.yml`:

| Переменная | По умолчанию | Описание |
|-----------|-------------|----------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/bank_db` | URL БД |
| `SPRING_DATASOURCE_USERNAME` | `bankuser` | Пользователь БД |
| `SPRING_DATASOURCE_PASSWORD` | `secret` | Пароль БД |
| `APP_JWT_SECRET` | `my-super-secret-key-minimum-32-characters-long` | Секрет для подписи JWT |
| `APP_JWT_EXPIRATION_MS` | `86400000` | Срок жизни токена (мс), по умолчанию 24 часа |
