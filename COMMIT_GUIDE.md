# Git Commit Guide

## 📋 Pre-commit Checklist

✅ Đã xóa các file dư thừa:
- ❌ app-run.log
- ❌ FINAL_FIX.md
- ❌ MIGRATION_COMPLETE.md
- ❌ MYSQL_ONLY.md
- ❌ debug.sh, force-reload.sh, run-debug.sh, run-direct.sh, run-simple.sh
- ❌ target/ directory
- ❌ .idea/ directory

✅ Đã cập nhật:
- ✓ .gitignore (đầy đủ)
- ✓ README.md (chi tiết)
- ✓ RedisConfig.java (với @ConditionalOnProperty)
- ✓ application.properties (app.redis.enabled=false)

✅ Files giữ lại (cần thiết):
- ✓ Source code (src/)
- ✓ Configuration files
- ✓ Docker files
- ✓ Maven files
- ✓ Documentation

## 🚀 Các bước commit

### 1. Kiểm tra status
```bash
git status
```

### 2. Add tất cả files
```bash
git add .
```

### 3. Commit với message rõ ràng
```bash
git commit -m "Initial commit: Product Order API with Spring Boot

Features:
- REST API for Product and Order management
- MySQL + H2 database support
- Optional Redis caching
- Optional Elasticsearch search
- Docker support with docker-compose
- Swagger API documentation
- Exception handling and validation
- Transaction management

Tech stack:
- Java 17
- Spring Boot 3.2.0
- MySQL 8.0
- Redis (optional)
- Elasticsearch (optional)
- Docker & Docker Compose
"
```

### 4. Kiểm tra log
```bash
git log --oneline
```

## 📦 Cấu trúc commit (nếu cần chi tiết hơn)

### Option 1: Single commit (Recommended cho lần đầu)
```bash
git add .
git commit -m "Initial commit: Complete Product Order API"
```

### Option 2: Multiple commits (Nếu muốn tách nhỏ)
```bash
# Core application
git add src/main/java/com/example/productorder/*.java
git add src/main/java/com/example/productorder/model/
git add src/main/java/com/example/productorder/repository/
git add src/main/java/com/example/productorder/service/
git add src/main/java/com/example/productorder/controller/
git commit -m "feat: Add core application with Product and Order management"

# Configuration
git add src/main/java/com/example/productorder/config/
git add src/main/resources/
git commit -m "feat: Add configuration for Redis, Elasticsearch and OpenAPI"

# Docker setup
git add Dockerfile docker-compose.yml docker-start.sh init-db/
git commit -m "feat: Add Docker configuration"

# Documentation
git add README.md api-test.http
git commit -m "docs: Add comprehensive documentation"

# Build configuration
git add pom.xml .gitignore .dockerignore mvnw .mvn/
git commit -m "build: Add Maven configuration and build files"
```

## 🔍 Kiểm tra trước khi push

```bash
# Xem tất cả files sẽ được commit
git ls-tree -r HEAD --name-only

# Kiểm tra ignored files
git status --ignored

# Kiểm tra diff
git diff --cached
```

## 📤 Push lên remote repository

```bash
# Nếu chưa có remote
git remote add origin <repository-url>

# Push
git push -u origin main
# hoặc
git push -u origin master
```

## ✅ Checklist cuối cùng

- [ ] Không có file .log
- [ ] Không có thư mục target/
- [ ] Không có thư mục .idea/
- [ ] Không có file .DS_Store
- [ ] README.md đầy đủ thông tin
- [ ] .gitignore đầy đủ
- [ ] Code compile thành công
- [ ] Application chạy được

## 🎯 Best Practices

1. **Commit message format:**
   ```
   <type>: <subject>
   
   <body>
   ```
   
   Types: feat, fix, docs, style, refactor, test, chore

2. **Commit thường xuyên:** Commit sau mỗi feature hoàn thành

3. **Message rõ ràng:** Mô tả những gì đã thay đổi và tại sao

4. **Không commit:** Generated files, logs, IDE settings, dependencies

## 📝 Example Commit Messages

```bash
# Good ✅
git commit -m "feat: Add Redis caching for product queries"
git commit -m "fix: Resolve Redis connection error when disabled"
git commit -m "docs: Update README with Docker instructions"

# Bad ❌
git commit -m "update"
git commit -m "fix bug"
git commit -m "changes"
```

---

**Note:** File này chỉ để hướng dẫn, không cần commit file này nếu không muốn.

