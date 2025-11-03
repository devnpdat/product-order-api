# Fix IntelliJ IDEA Build Error

## ❌ Lỗi gốc:
```
java: JDK isn't specified for module 'product-order-api'
Errors occurred while compiling module 'product-order-api'
```

## ✅ Giải pháp đã áp dụng:

### 1. Đã tạo các file cấu hình IntelliJ IDEA:

**`.idea/misc.xml`** - Cấu hình JDK cho project:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="JDK_17" default="true" project-jdk-name="17" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/out" />
  </component>
</project>
```

**`.idea/compiler.xml`** - Cấu hình compiler target:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="CompilerConfiguration">
    <bytecodeTargetLevel target="17" />
  </component>
</project>
```

**`.idea/encodings.xml`** - Cấu hình encoding:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="Encoding">
    <file url="file://$PROJECT_DIR$/src/main/java" charset="UTF-8" />
    <file url="file://$PROJECT_DIR$/src/main/resources" charset="UTF-8" />
  </component>
</project>
```

**`.idea/vcs.xml`** - Cấu hình Git:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="VcsDirectoryMappings">
    <mapping directory="$PROJECT_DIR$" vcs="Git" />
  </component>
</project>
```

### 2. Đã cập nhật `.gitignore`:
```gitignore
### IntelliJ IDEA ###
.idea/*
!.idea/compiler.xml
!.idea/encodings.xml
!.idea/misc.xml
!.idea/vcs.xml
!.idea/modules.xml
*.iws
*.iml
*.ipr
out/
```

Chỉ commit các file cấu hình cần thiết, ignore các file cá nhân như `workspace.xml`.

## 🔧 Các bước thực hiện trong IntelliJ IDEA:

### Bước 1: Invalidate Caches and Restart
1. Mở IntelliJ IDEA
2. Vào **File** → **Invalidate Caches...**
3. Chọn **Invalidate and Restart**

### Bước 2: Configure Project SDK
1. Vào **File** → **Project Structure** (hoặc `Cmd + ;` trên Mac)
2. Tab **Project**:
   - **SDK**: Chọn JDK 17 (nếu chưa có, click **Add SDK** → **Download JDK**)
   - **Language level**: 17 - Sealed types, always-strict floating-point semantics
3. Tab **Modules**:
   - Chọn module **product-order-api**
   - **Language level**: Project default (17)
   - **Dependencies**: Kiểm tra Maven dependencies đã được import
4. Click **OK**

### Bước 3: Reload Maven Project
1. Mở Maven tool window (bên phải)
2. Click icon **Reload All Maven Projects** (🔄)
3. Đợi Maven download dependencies và build

### Bước 4: Build Project
1. Vào **Build** → **Rebuild Project**
2. Hoặc sử dụng shortcut: `Cmd + Shift + F9` (Mac) hoặc `Ctrl + Shift + F9` (Windows)

## 🎯 Kiểm tra:

### Build thành công nếu thấy:
```
Build completed successfully in X ms
```

### Nếu vẫn lỗi:

#### A. Kiểm tra JDK đã cài đặt:
```bash
java -version
```
Kết quả phải là: `openjdk version "17.x.x"` hoặc `java version "17.x.x"`

#### B. Kiểm tra Maven build:
```bash
mvn clean compile
```
Nếu Maven build thành công nhưng IntelliJ vẫn lỗi → IntelliJ settings chưa đúng.

#### C. Configure JDK manually:
1. **File** → **Project Structure** → **Platform Settings** → **SDKs**
2. Click **+** → **Add JDK**
3. Chọn đường dẫn JDK 17:
   - Mac: `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home`
   - Windows: `C:\Program Files\Java\jdk-17`
   - Linux: `/usr/lib/jvm/java-17-openjdk`

#### D. Reimport Maven project:
1. Right-click vào `pom.xml`
2. Chọn **Maven** → **Reimport**

#### E. Check Compiler settings:
1. **Settings/Preferences** → **Build, Execution, Deployment** → **Compiler** → **Java Compiler**
2. **Project bytecode version**: 17
3. **Target bytecode version** cho module: 17

## 📋 Verification Checklist:

- [ ] JDK 17 đã được cài đặt
- [ ] IntelliJ IDEA đã configured với JDK 17
- [ ] Maven dependencies đã được download
- [ ] File `.idea/misc.xml` có `languageLevel="JDK_17"`
- [ ] File `.idea/compiler.xml` có `target="17"`
- [ ] Maven build thành công: `mvn clean compile`
- [ ] IntelliJ build thành công: **Build** → **Rebuild Project**

## 🚀 Sau khi fix:

Bây giờ bạn có thể:
1. ✅ Build project trong IntelliJ IDEA
2. ✅ Run/Debug application
3. ✅ Use IntelliJ features (code completion, refactoring, etc.)
4. ✅ Commit code với cấu hình IntelliJ cơ bản

## 📝 Notes:

- Các file trong `.idea/` (trừ workspace.xml) đã được commit để team members khác có cùng cấu hình
- File `workspace.xml` bị ignore vì chứa settings cá nhân
- File `*.iml` bị ignore và sẽ được IntelliJ tự tạo từ Maven

---

**✨ Happy coding!**

