# 🔧 НЕМЕДЛЕННЫЕ ДЕЙСТВИЯ ДЛЯ ИСПРАВЛЕНИЯ

## 1. Удалить пустые Impl-классы (в терминале)

```bash
# Удалить пустые реализации репозиториев
rm /Users/aleskey/Code/Spring/bank_rest/src/main/java/com/example/bankcards/repository/CardRepositoryImpl.java
rm /Users/aleskey/Code/Spring/bank_rest/src/main/java/com/example/bankcards/repository/UserRepositoryImpl.java

# Проверить, что осталось
ls -la /Users/aleskey/Code/Spring/bank_rest/src/main/java/com/example/bankcards/repository/
```

**Почему?** 
- Это пустые заглушки, которые не реализуют никакой логики
- Spring Data JpaRepository уже предоставляет всю функциональность
- Impl-классы только добавляют путаницу и ошибки компиляции

---

## 2. Проверить структуру после удаления

После удаления Impl-классов в папке `repository/` должны быть:
```
repository/
├── Interfaces/
│   ├── CardRepository.java      ✅ нужен (интерфейс)
│   └── UserRepository.java      ✅ нужен (интерфейс)
└── README_Repository.md         (можно удалить позже)
```

---

## 3. Исправления уже сделаны

### ✅ CardService.java
- [x] Исправлен импорт: `java.awt.print.Pageable` → `org.springframework.data.domain.Pageable`

---

## 4. Следующие шаги

После этих действий проект должен компилироваться с минимальными ошибками.

### Проверьте компиляцию:
```bash
cd /Users/aleskey/Code/Spring/bank_rest
mvn clean compile
```

### Если всё компилируется, переходите к:

1. **Реализовать CardServiceImpl**
   - Методы для получения карт
   - Методы для переводов
   - Методы для блокировки

2. **Реализовать AuthService**
   - Аутентификация
   - Генерация JWT

3. **Configurить Security**
   - SecurityConfig
   - JWT Filter
   - UserDetailsService

4. **Реализовать контроллеры**
   - AuthController
   - Завершить CardController

---

**Статус:** После этих действий проект перейдёт на этап 5 (Сервисы).

