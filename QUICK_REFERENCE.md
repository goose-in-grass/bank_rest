# ⚡ QUICK REFERENCE: Что сделано vs Что нужно

## ✅ Готово (Не трогать!)

```
✅ Sущности (User, Card)
   - Правильные поля
   - Правильные связи
   - Lombok оптимизация
   - Enum'ы (Role, CardStatus)

✅ DTO (6 классов)
   - CreateCardRequest
   - LoginRequest
   - TransferRequest
   - CardResponse
   - LoginResponse
   - TransferResponse
   - Все валидированы
   - Все используют Lombok

✅ БД (Liquibase)
   - 001-create-users-table.yaml
   - 002-create-cards-table.yaml
   - 003-insert-admin-user.yaml
   - Foreign keys настроены
   - Индексы на unique

✅ Конфигурация
   - pom.xml правильный
   - application.yml готовый
   - docker-compose.yml готовый

✅ Сегодня исправлено
   - CardService: java.awt.print.Pageable → org.springframework.data.domain.Pageable
   - Card.java: удалён неиспользуемый интерфейс
   - Все DTO применены лучшие практики
```

---

## ❌ КРИТИЧНО: Удалить эти файлы

> Эти файлы нужно удалить, потому что они пустые и мешают

```
❌ /repository/CardRepositoryImpl.java (166 строк пустого кода)
❌ /repository/UserRepositoryImpl.java (181 строка пустого кода)
```

**Команда для удаления:**
```bash
cd /Users/aleskey/Code/Spring/bank_rest
rm src/main/java/com/example/bankcards/repository/CardRepositoryImpl.java
rm src/main/java/com/example/bankcards/repository/UserRepositoryImpl.java
mvn clean compile
```

---

## ❌ Нужно сделать (по приоритетам)

### 1️⃣ Сервис-слой (CardServiceImpl)

```java
// Файл: src/main/java/com/example/bankcards/service/CardServiceImpl.java

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {
    
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    
    // Методы для пользователя:
    ❌ Page<CardResponse> getMyCards(Long userId, Pageable p, String status)
    ❌ BigDecimal getBalance(Long cardId, Long userId)
    ❌ void requestBlock(Long cardId, Long userId)
    ❌ void transfer(TransferRequest req, Long userId)
    
    // Методы для администратора:
    ❌ CardResponse createCard(CreateCardRequest req)
    ❌ void blockCard(Long cardId)
    ❌ void activateCard(Long cardId)
    ❌ void deleteCard(Long cardId)
    ❌ Page<CardResponse> getAllCards(Pageable p, String status)
}
```

**Логика для transfer():**
```
1. Найти обе карты по номерам
2. Проверить, что владелец обеих = userId
3. Проверить статусы карт = ACTIVE
4. Проверить balance >= amount
5. Запустить @Transactional операцию:
   - fromCard.balance -= amount
   - toCard.balance += amount
   - сохранить обе карты
6. Вернуть результат
```

### 2️⃣ Security-слой

```java
// Файл: src/main/java/com/example/bankcards/security/JwtTokenProvider.java
❌ generateToken(User user): String
❌ extractUserId(String token): Long
❌ validateToken(String token): boolean
❌ getExpiresIn(): Long

// Файл: src/main/java/com/example/bankcards/security/JwtAuthenticationFilter.java
❌ filter(HttpServletRequest, HttpServletResponse, FilterChain)

// Файл: src/main/java/com/example/bankcards/security/CustomUserDetailsService.java
❌ loadUserByUsername(String username): UserDetails

// Файл: src/main/java/com/example/bankcards/config/SecurityConfig.java
❌ securityFilterChain(HttpSecurity): SecurityFilterChain
❌ authenticate(AuthenticationManager, credentials): Authentication
❌ passwordEncoder(): PasswordEncoder
```

### 3️⃣ Контроллеры

```java
// Файл: src/main/java/com/example/bankcards/controller/AuthController.java
❌ POST /api/auth/login (LoginRequest) → LoginResponse

// Файл: src/main/java/com/example/bankcards/controller/CardController.java
❌ GET /api/cards (с пагинацией) → Page<CardResponse>
❌ GET /api/cards/{id}/balance → BigDecimal
❌ POST /api/cards/transfer (TransferRequest) → TransferResponse
❌ POST /api/cards/{id}/block → void

// Файл: src/main/java/com/example/bankcards/controller/AdminCardController.java (новый)
❌ POST /api/admin/cards (CreateCardRequest) → CardResponse
❌ DELETE /api/admin/cards/{id} → void
❌ POST /api/admin/cards/{id}/block → void
❌ POST /api/admin/cards/{id}/activate → void
❌ GET /api/admin/cards (с пагинацией) → Page<CardResponse>
```

### 4️⃣ Error Handling

```java
// Файл: src/main/java/com/example/bankcards/exception/GlobalExceptionHandler.java
❌ @RestControllerAdvice
❌ handle NotFoundException
❌ handle InsufficientFundsException
❌ handle ValidationException
❌ handle AccessDeniedException
❌ Вернуть единый ErrorResponse

// Новые классы исключений:
❌ NotFoundException extends RuntimeException
❌ InsufficientFundsException extends RuntimeException
❌ InvalidCardStatusException extends RuntimeException
❌ AccessDeniedException extends RuntimeException
```

### 5️⃣ MapStruct Mapper

```java
// Файл: src/main/java/com/example/bankcards/mapper/CardMapper.java
❌ @Mapper(componentModel = "spring")
❌ toResponse(Card card): CardResponse
❌ toEntity(CreateCardRequest request): Card
```

### 6️⃣ OpenAPI / Swagger

```java
// В CardController:
❌ @Operation(summary = "Get my cards")
❌ @ApiResponse(responseCode = "200")
❌ @ApiResponse(responseCode = "401")

// В AuthController:
❌ @Operation(summary = "User login")
❌ @ApiResponse(responseCode = "200")
❌ @ApiResponse(responseCode = "401")
```

### 7️⃣ Тесты

```java
// Файл: src/test/java/com/example/bankcards/service/CardServiceTest.java
❌ testTransferSuccessful()
❌ testTransferInsufficientFunds()
❌ testTransferBlockedCard()
❌ testAccessToOtherUserCard()

// Файл: src/test/java/com/example/bankcards/controller/AuthControllerTest.java
❌ testLoginSuccess()
❌ testLoginInvalidPassword()

// Файл: src/test/java/com/example/bankcards/controller/CardControllerTest.java
❌ testGetMyCardsAuthenticated()
❌ testGetMyCardsNotAuthenticated()
```

---

## 📋 Checklist: Что нужно сделать по порядку

```
ДЕНЬ 1 (Сегодня):
  [ ] Удалить CardRepositoryImpl.java и UserRepositoryImpl.java
  [ ] Запустить mvn clean compile
  [ ] Убедиться компилируется ✅

ДЕНЬ 2-3 (Сервисы):
  [ ] Реализовать CardServiceImpl (все методы)
  [ ] Реализовать AuthService (login, token generation)
  [ ] Написать маппер для Entity → DTO

ДЕНЬ 3-4 (Security):
  [ ] Configurировать SecurityConfig
  [ ] Реализовать JwtTokenProvider
  [ ] Реализовать JwtAuthenticationFilter
  [ ] Реализовать CustomUserDetailsService

ДЕНЬ 4-5 (Контроллеры):
  [ ] Реализовать AuthController (/api/auth/login)
  [ ] Завершить CardController (все эндпоинты)
  [ ] Создать AdminCardController (админ-эндпоинты)

ДЕНЬ 6 (Ошибки и документация):
  [ ] Реализовать GlobalExceptionHandler
  [ ] Создать классы исключений
  [ ] Добавить @Operation и @ApiResponse

ДЕНЬ 7 (Последние штрихи):
  [ ] Написать базовые тесты
  [ ] Обновить README
  [ ] Протестировать через Postman/Swagger
  [ ] Проверить миграции БД
```

---

## 🧪 Как проверить, что всё работает

### 1. Компиляция
```bash
mvn clean compile
# ✅ Должно завершиться без ошибок (только предупреждения OK)
```

### 2. Docker и БД
```bash
docker compose up
# ✅ PostgreSQL должна запуститься
# ✅ Миграции должны примениться
```

### 3. Запуск приложения
```bash
mvn spring-boot:run
# ✅ Приложение должно запуститься на порту 8080
```

### 4. Тестировать API
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Get cards
curl http://localhost:8080/api/cards \
  -H "Authorization: Bearer TOKEN"
```

### 5. Swagger
```
http://localhost:8080/swagger-ui/index.html
```

---

## 📊 Статистика того, что нужно написать

| Компонент | Строк кода | Время |
|-----------|-----------|--------|
| CardServiceImpl | ~300 | 2-3 часа |
| AuthService | ~150 | 1 час |
| Security Config | ~400 | 2-3 часа |
| Контроллеры | ~300 | 2 часа |
| ExceptionHandler | ~150 | 1 час |
| Mapper | ~50 | 30 мин |
| Тесты | ~200 | 2 часа |
| **ИТОГО** | **~1550** | **~14 часов** |

**При 4 часах в день: 4 дня разработки**

---

## ⚡ Самое важное

1. ✅ **Сегодня удалить Impl-классы** — это блокер
2. ✅ **Реализовать CardServiceImpl** — это основа
3. ✅ **Configurировать Security** — это безопасность
4. ✅ **Завершить контроллеры** — это API
5. ✅ **Написать тесты** — это проверка

**Порядок важен! Не переходите к следующему, пока предыдущее не работает.**

---

## 📞 Если что-то не понятно

Файлы с документацией:
- `STATUS_SUMMARY.md` — этот файл
- `COMPLIANCE_REPORT.md` — детальный анализ
- `NEXT_STEPS.md` — пошаговые инструкции
- `ROADMAP.md` — исходный план

---

**Удачи! Вы почти готовы к разработке! 🚀**

