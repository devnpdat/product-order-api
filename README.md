# Product Order API - Spring Boot

API REST để quản lý sản phẩm và đơn hàng được xây dựng bằng Spring Boot với tích hợp MySQL, Redis Cache và Elasticsearch.

## 🚀 Công nghệ sử dụng

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL 8.0** (Production)
- **H2 Database** (Development)
- **Redis** (Caching - Optional)
- **Elasticsearch** (Search - Optional)
- **Docker & Docker Compose**
- **Lombok**
- **Maven**
- **Swagger/OpenAPI** (API Documentation)

## 📁 Cấu trúc Project

```
src/
├── main/
│   ├── java/com/example/productorder/
│   │   ├── controller/       # REST Controllers
│   │   ├── service/          # Business Logic
│   │   ├── repository/       # Data Access Layer (JPA + Elasticsearch)
│   │   ├── model/            # JPA Entities
│   │   ├── document/         # Elasticsearch Documents
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── config/           # Configuration Classes
│   │   ├── exception/        # Exception Handling
│   │   └── ProductOrderApplication.java
│   └── resources/
│       ├── application.properties          # Default config
│       ├── application-docker.properties   # Docker config
│       ├── application-prod.properties     # Production config
│       └── application-test.properties     # Test config
└── test/
```

## 🔧 Cài đặt và Chạy

### Yêu cầu
- JDK 17 trở lên
- Maven 3.6+
- Docker & Docker Compose (cho môi trường production)

### Chạy ứng dụng - Development Mode (H2 Database)

```bash
# Clone hoặc navigate to project directory
cd product-order-api

# Build project
mvn clean install

# Run application with H2 (in-memory database)
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

### Chạy ứng dụng - Production Mode (Docker + MySQL)

```bash
# Build và chạy với Docker Compose
./docker-start.sh

# Hoặc chạy thủ công:
docker-compose up -d
```

Services:
- API: `http://localhost:8080`
- MySQL: `localhost:3307`
- Redis: `localhost:6380` (nếu enabled)
- Elasticsearch: `http://localhost:9201` (nếu enabled)

### Dừng services

```bash
docker-compose down
```

## 🔌 Configuration

### Redis (Optional)
Để bật Redis caching, cập nhật `application.properties`:
```properties
app.redis.enabled=true
spring.redis.host=localhost
spring.redis.port=6379
```

### Elasticsearch (Optional)
Elasticsearch được cấu hình tự động khi service khả dụng. Service sẽ fallback về database search nếu Elasticsearch không available.

## 📚 API Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 📖 API Endpoints

### Product APIs

#### 1. Lấy tất cả sản phẩm
```
GET /api/products
```

#### 2. Lấy sản phẩm theo ID
```
GET /api/products/{id}
```

#### 3. Tìm kiếm sản phẩm theo tên
```
GET /api/products/search?name={name}
```

#### 4. Tạo sản phẩm mới
```
POST /api/products
Content-Type: application/json

{
  "name": "iPhone 15",
  "description": "Latest iPhone model",
  "price": 999.99,
  "stock": 100
}
```

#### 5. Cập nhật sản phẩm
```
PUT /api/products/{id}
Content-Type: application/json

{
  "name": "iPhone 15 Pro",
  "description": "Pro model",
  "price": 1199.99,
  "stock": 50
}
```

#### 6. Xóa sản phẩm
```
DELETE /api/products/{id}
```

#### 7. Cập nhật số lượng tồn kho
```
PATCH /api/products/{id}/stock?quantity=10
```

### Order APIs

#### 1. Lấy tất cả đơn hàng
```
GET /api/orders
```

#### 2. Lấy đơn hàng theo ID
```
GET /api/orders/{id}
```

#### 3. Lấy đơn hàng theo Order Number
```
GET /api/orders/number/{orderNumber}
```

#### 4. Lấy đơn hàng theo trạng thái
```
GET /api/orders/status/{status}
```
Status: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED

#### 5. Tạo đơn hàng mới
```
POST /api/orders
Content-Type: application/json

{
  "customerName": "Nguyen Van A",
  "customerEmail": "nguyenvana@example.com",
  "customerPhone": "0123456789",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

#### 6. Cập nhật trạng thái đơn hàng
```
PATCH /api/orders/{id}/status?status=CONFIRMED
```

#### 7. Hủy đơn hàng
```
DELETE /api/orders/{id}
```

### Admin APIs

#### Reindex Elasticsearch
```
POST /api/admin/reindex-products
```

## 🧪 Testing

File test API: `api-test.http`

## 📝 Features

- ✅ CRUD operations cho Products và Orders
- ✅ Search functionality với Elasticsearch (với fallback)
- ✅ Redis caching cho performance
- ✅ Transaction management
- ✅ Exception handling
- ✅ API documentation với Swagger
- ✅ Docker support
- ✅ Multiple environment profiles
- ✅ Automatic stock management
- ✅ Order status tracking

## 🛠️ Development Notes

### Redis Configuration
Redis được cấu hình với `@ConditionalOnProperty` để ứng dụng có thể chạy mà không cần Redis. Để enable Redis:
- Set `app.redis.enabled=true`
- Đảm bảo Redis server đang chạy

### Elasticsearch Configuration
Elasticsearch repository được inject với `@Autowired(required = false)`, cho phép ứng dụng hoạt động bình thường khi Elasticsearch không available.

## 📄 License

MIT License

## 👤 Author

Product Order API Team

#### 6. Cập nhật trạng thái đơn hàng
```
PATCH /api/orders/{id}/status?status=CONFIRMED
```

#### 7. Hủy đơn hàng
```
POST /api/orders/{id}/cancel
```

## Database

Ứng dụng sử dụng H2 in-memory database.

### H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:productorderdb`
- Username: `sa`
- Password: (để trống)

## Tính năng

✅ CRUD sản phẩm  
✅ CRUD đơn hàng  
✅ Quản lý tồn kho tự động  
✅ Validation dữ liệu đầu vào  
✅ Exception handling toàn cục  
✅ Kiểm tra số lượng tồn kho khi đặt hàng  
✅ Tự động tạo mã đơn hàng  
✅ Khôi phục tồn kho khi hủy đơn  

## Test API với cURL

### Tạo sản phẩm:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Dell XPS 15",
    "description": "High performance laptop",
    "price": 1500.00,
    "stock": 20
  }'
```

### Tạo đơn hàng:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Nguyen Van A",
    "customerEmail": "nguyenvana@example.com",
    "customerPhone": "0123456789",
    "items": [
      {"productId": 1, "quantity": 2}
    ]
  }'
```

## Mở project trong IntelliJ IDEA

1. File → Open
2. Chọn thư mục `product-order-api`
3. Chờ Maven import dependencies
4. Run `ProductOrderApplication.java`

## License

MIT License

