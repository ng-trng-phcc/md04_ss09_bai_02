# md04_ss09_bai_01 - Pharmacy Service + Config Server

**Ngày:** 21-09-2026 | md04_ss09_bài_01 | tạo pharmacy-service, add yaml lên git, cấu hình config server

## Cấu trúc
- `pharmacy-service/` - Client (ss09/pharmacy-service) - Port 8085, lấy config từ Config Server, @Value hiển thị chi nhánh
- `config-server/` - Config Server (ss07/config-server) - Port 8888, đọc từ GitHub https://github.com/ng-trng-phcc/pharmacy-service-config và file:///home/white_ember/Code/md04/ss07/medical-config-repo
- `pharmacy-service-config/` - Bản sao yaml từ GitHub repo pharmacy-service-config (pharmacy-service.yml)
- `medical-config-repo/` - Local file repo chứa pharmacy-service.yml (pharmacy_db, Nha Thuoc So 1, 19001234)
- `medical-discovery-server/` - Eureka Server 8761

## Cấu hình chính (pharmacy-service.yml)
```yaml
spring.datasource.url: jdbc:postgresql://localhost:5432/pharmacy_db
app.branch-name: "Nha Thuoc So 1"
app.hotline: "19001234"
```

## Config Server (8888)
`config-server/src/main/resources/application.yaml`:
```yaml
spring.cloud.config.server.composite:
  - type: git
    uri: file:///home/white_ember/Code/md04/ss07/medical-config-repo
  - type: git
    uri: https://github.com/ng-trng-phcc/pharmacy-service-config
```

## Client
`pharmacy-service/src/main/resources/application.yaml`:
```yaml
spring.config.import: optional:configserver:http://localhost:8888
```
`BranchInfoLogger.java` dùng `@Value("${app.branch-name}")` in ra khi khởi động.
`PharmacyController.java` endpoint `/api/v1/pharmacy/branch` với `@RefreshScope`.

## Flow khởi động
1. PostgreSQL + tạo DB pharmacy_db
2. Config Server 8888
3. Eureka 8761
4. pharmacy-service 8085 -> fetch config -> in Chi nhánh

## Push
Repo này push lên https://github.com/ng-trng-phcc/md04_ss09_bai_01
Commit: "21-09-2026 | md04_ss09_bài_01 | tạo pharmacy-service, add yaml lên git, cấu hình config server"
