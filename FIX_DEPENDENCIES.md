# FIX: Cannot resolve symbol errors in IntelliJ IDEA

## ❌ Vấn đề hiện tại:

IntelliJ IDEA không thể resolve ANY symbols từ Maven dependencies:
- `Cannot resolve symbol 'io'` (Swagger/OpenAPI)
- `Cannot resolve symbol 'springframework'` (Spring Framework)
- `Cannot resolve symbol 'lombok'` (Lombok)
- `Cannot resolve symbol 'jakarta'` (Jakarta Validation)

## 🎯 Root Cause:

IntelliJ IDEA chưa import Maven dependencies vào project. Dependencies đã có trong `pom.xml` nhưng IntelliJ chưa download/index chúng.

---

## ✅ GIẢI PHÁP - Làm CHÍNH XÁC theo thứ tự:

### 🔴 Bước 1: Force Reload Maven Project (QUAN TRỌNG NHẤT!)

#### Cách 1: Từ Maven Tool Window
1. Mở **Maven** tool window:
   - **View** → **Tool Windows** → **Maven**
   - Hoặc click icon Maven ở bên phải màn hình
   - Hoặc `Cmd + Shift + A` → gõ "Maven" → chọn "Maven"

2. Trong Maven tool window:
   - Click vào icon **🔄 Reload All Maven Projects** (góc trên bên trái)
   - Đợi IntelliJ download tất cả dependencies (có thể mất 2-5 phút)
   - Xem progress bar ở dưới cùng màn hình

#### Cách 2: Từ pom.xml
1. Mở file `pom.xml`
2. Right-click vào file → **Maven** → **Reload Project**
3. Đợi download hoàn tất

#### Cách 3: Từ Action Menu
1. `Cmd + Shift + A` (Mac) hoặc `Ctrl + Shift + A` (Windows)
2. Gõ: **"Reload All Maven Projects"**
3. Enter

---

### 🟡 Bước 2: Invalidate Caches and Restart

1. **File** → **Invalidate Caches...**
2. Chọn các options:
   - ✅ **Clear file system cache and Local History**
   - ✅ **Clear VCS Log caches and indexes**
   - ✅ **Clear downloaded shared indexes**
3. Click **Invalidate and Restart**
4. Đợi IntelliJ restart và re-index project (3-5 phút)

---

### 🟢 Bước 3: Configure Project Structure

1. **File** → **Project Structure** (`Cmd + ;` trên Mac)

2. **Tab "Project":**
   ```
   Name: product-order-api
   SDK: 17 (java version "17.x.x")
   Language level: 17 - Sealed types, always-strict floating-point semantics
   ```
   
   Nếu không có SDK 17:
   - Click **Edit** → **Add SDK** → **Download JDK**
   - Chọn vendor: **Eclipse Temurin (AdoptOpenJDK HotSpot)** hoặc **Amazon Corretto**
   - Version: **17**
   - Click **Download**

3. **Tab "Modules":**
   - Nên thấy module **product-order-api**
   - Nếu không thấy → Click **+** → **Import Module** → chọn `pom.xml`
   - **Sources**: `src/main/java` (màu xanh)
   - **Resources**: `src/main/resources` (màu tím)
   - **Test Sources**: `src/test/java` (màu xanh lá)
   - **Dependencies**: Phải thấy tất cả Maven dependencies

4. **Tab "Libraries":**
   - Phải thấy **Maven: org.springframework.boot:spring-boot-starter-web:...**
   - Phải thấy **Maven: org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0**
   - Phải thấy **Maven: org.projectlombok:lombok:...**
   - Nếu KHÔNG thấy → Quay lại Bước 1!

5. Click **OK**

---

### 🔵 Bước 4: Verify Maven Dependencies Downloaded

1. Mở Terminal trong IntelliJ: **View** → **Tool Windows** → **Terminal**

2. Chạy lệnh:
   ```bash
   mvn dependency:tree | head -50
   ```

3. Phải thấy output như:
   ```
   [INFO] com.example:product-order-api:jar:1.0.0
   [INFO] +- org.springframework.boot:spring-boot-starter-web:jar:3.2.0:compile
   [INFO] |  +- org.springframework.boot:spring-boot-starter:jar:3.2.0:compile
   [INFO] +- org.springdoc:springdoc-openapi-starter-webmvc-ui:jar:2.2.0:compile
   [INFO] +- org.projectlombok:lombok:jar:1.18.30:compile (optional)
   ...
   ```

4. Nếu thấy ERROR → Chạy:
   ```bash
   mvn clean install -U
   ```
   Flag `-U` force update tất cả dependencies.

---

### 🟣 Bước 5: Reimport Maven Project (Nếu vẫn lỗi)

1. **File** → **Close Project**
2. Từ Welcome Screen → **Open**
3. Chọn thư mục project
4. IntelliJ sẽ tự động detect `pom.xml` và hỏi **"Maven projects need to be imported"**
5. Click **Import Changes** hoặc **Enable Auto-Import**
6. Đợi import và indexing hoàn tất

---

### ⚫ Bước 6: Enable Annotation Processing (Cho Lombok)

1. **Settings/Preferences** (`Cmd + ,` trên Mac)
2. **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
3. ✅ Check **Enable annotation processing**
4. **Obtain processors from project classpath**
5. Click **OK**

---

### 🟤 Bước 7: Rebuild Project

1. **Build** → **Rebuild Project**
2. Hoặc `Cmd + Shift + F9` (Mac) / `Ctrl + Shift + F9` (Windows)
3. Đợi build hoàn tất
4. Check Build output ở dưới cùng

---

## 🔍 Verification - Kiểm tra sau khi làm xong:

### ✅ Check 1: Maven Dependencies trong External Libraries
```
Project Structure → Libraries
Hoặc: Project view → External Libraries

Phải thấy:
- Maven: org.springframework.boot:spring-boot-starter-web:3.2.0
- Maven: org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0
- Maven: org.projectlombok:lombok:1.18.30
- Maven: com.mysql:mysql-connector-j:...
- và nhiều dependencies khác
```

### ✅ Check 2: Imports không còn màu đỏ
```java
import io.swagger.v3.oas.annotations.Operation;  // ← Không màu đỏ
import org.springframework.web.bind.annotation.*;  // ← Không màu đỏ
import lombok.RequiredArgsConstructor;  // ← Không màu đỏ
```

### ✅ Check 3: Auto-completion hoạt động
- Gõ `@Rest` → phải thấy suggestion `@RestController`
- Gõ `@Get` → phải thấy suggestion `@GetMapping`
- Gõ `ResponseEntity.` → phải thấy các methods như `.ok()`, `.status()`, etc.

### ✅ Check 4: Build thành công
```
Build → Rebuild Project
→ "Build completed successfully"
```

### ✅ Check 5: Run application
```
Right-click ProductOrderApplication.java
→ Run 'ProductOrderApplication.main()'
→ Application khởi động không lỗi
```

---

## 🚨 Nếu VẪN LỖI sau tất cả các bước trên:

### Solution A: Delete .idea và reimport
```bash
cd /Users/npdat132/Work2/Projects/PIM/product-order-api
rm -rf .idea/
rm -f *.iml

# Sau đó trong IntelliJ:
File → Close Project
File → Open → Chọn thư mục project
```

### Solution B: Delete Maven local repository cache
```bash
rm -rf ~/.m2/repository/org/springdoc/
rm -rf ~/.m2/repository/org/springframework/
rm -rf ~/.m2/repository/org/projectlombok/

# Trong IntelliJ Maven tool window:
Click 🔄 Reload All Maven Projects
```

### Solution C: Update Maven settings
```
Settings → Build, Execution, Deployment → Build Tools → Maven

Maven home path: Bundled (Maven 3)
User settings file: <empty>
Local repository: ~/.m2/repository

✅ Always update snapshots
✅ Use plugin registry
```

### Solution D: Check IntelliJ IDEA version
```
IntelliJ IDEA → About IntelliJ IDEA

Recommended: Version 2023.3 trở lên
Nếu quá cũ → Update IntelliJ IDEA
```

### Solution E: Maven offline mode
```
Maven tool window → Click "M" icon (góc trên)
→ Uncheck "Work Offline" nếu đang được check
```

---

## 📊 Common Issues & Solutions:

| Issue | Solution |
|-------|----------|
| Maven tool window không hiện | **View** → **Tool Windows** → **Maven** |
| Reload Maven không làm gì cả | Check internet connection, check Maven offline mode |
| Dependencies download rất chậm | Check proxy settings, hoặc đổi Maven mirror |
| Lombok không hoạt động | Enable Annotation Processing (Bước 6) |
| Spring annotations màu đỏ | Phải có `spring-boot-starter-web` dependency |
| Swagger annotations màu đỏ | Phải có `springdoc-openapi-starter-webmvc-ui:2.2.0` |

---

## 🎯 TL;DR - Quick Fix (Thử trước):

```
1. Maven tool window → Click 🔄 Reload All Maven Projects
2. File → Invalidate Caches → Invalidate and Restart
3. Build → Rebuild Project
```

Nếu vẫn lỗi → Làm đầy đủ 7 bước ở trên.

---

## 📝 Notes:

- Maven dependencies phải được download TỪ INTERNET
- Lần đầu tiên sẽ mất 5-10 phút để download tất cả
- Phải có internet connection
- Nếu có proxy → Configure trong Maven settings
- File `pom.xml` đã đúng, không cần sửa gì

---

**✨ Sau khi làm xong, tất cả errors sẽ biến mất!**

