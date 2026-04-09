 Tech Stack (Công nghệ sử dụng)
Backend: Java 17 (Temurin), Spring Boot 3.x
Build Tool: Gradle
Database: MySQL 8.x
Storage: MinIO (S3-Compatible Object Storage)
Containerization: Docker & Docker Compose
📌 1. Chuẩn bị môi trường (Prerequisites)
Trước khi bắt đầu, hãy đảm bảo máy của bạn đã cài đặt các công cụ sau:

☕ Java Development Kit (JDK)
Dự án yêu cầu JDK 17. Khuyên dùng bản Temurin 17 (LTS) để đảm bảo tính ổn định.

Kiểm tra phiên bản bằng lệnh: java -version

🐋 Docker & Docker Compose
Dùng để chạy các dịch vụ hạ tầng (MySQL, MinIO) một cách nhanh chóng mà không cần cài đặt rườm rà vào hệ điều hành.

Kiểm tra: docker --version và docker-compose --version

🐘 IntelliJ IDEA (Hoặc IDE bất kỳ)
Nên sử dụng IntelliJ IDEA bản Community hoặc Ultimate để có hỗ trợ tốt nhất cho Gradle và Spring Boot.

2. Thiết lập Hạ tầng (Infrastructure Setup)
   Dự án sử dụng Docker Compose để khởi chạy các dịch vụ lưu trữ. Việc này đảm bảo môi trường phát triển luôn đồng nhất và không tốn thời gian cài đặt thủ công.

2.1. Khởi chạy MySQL và MinIO
File cấu hình hạ tầng nằm tại: src/main/resources/docker-compose.yml. Để khởi chạy, mở Terminal tại thư mục gốc dự án và chạy lệnh:

PowerShell
docker-compose -f src/main/resources/docker-compose.yml up -d
MySQL: Chạy tại port 3306.

MinIO Console: Truy cập tại http://localhost:9001 (User: minioadmin / Pass: minioadmin123).

2.2. Cấu hình quyền truy cập MinIO (CLI)
Sau khi MinIO khởi chạy, cần cấp quyền truy cập công khai cho bucket indiemusic để hỗ trợ việc streaming nhạc trực tiếp. Thực hiện các lệnh sau:

Thiết lập bí danh (Alias) kết nối:

PowerShell
docker-compose -f src/main/resources/docker-compose.yml exec minio mc alias set myminio http://localhost:9000 minioadmin minioadmin123
Cấp quyền 'download' (Public Read-only) cho bucket:

PowerShell
docker-compose -f src/main/resources/docker-compose.yml exec minio mc anonymous set d

3. Cấu hình Ứng dụng (Application Configuration)
   Dự án sử dụng cơ chế Environment Separation để đảm bảo không lộ thông tin bảo mật (như mật khẩu Database hay Secret Key) lên Git.

3.1. Thiết lập file Properties
Tìm file src/main/resources/application-local.properties.example.

Tạo một bản sao của file này và đổi tên thành application.properties.

Mở file application.properties và điền thông tin cấu hình thực tế của bạn:

Properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/rubysparks_db?createDatabaseIfNotExist=true
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

# MinIO Configuration
minio.endpoint=http://127.0.0.1:9000
minio.access-key=YOUR_ACCESS_KEY
minio.secret-key=YOUR_SECRET_KEY
Lưu ý: File application.properties đã được liệt kê trong .gitignore và sẽ không được đẩy lên repository.

3.2. Chạy ứng dụng bằng Gradle
Sau khi cấu hình xong, bạn có thể khởi động ứng dụng bằng lệnh:

PowerShell
./gradlew bootRun
Hoặc chạy trực tiếp file RubySparksApplication.java trong IntelliJ IDEA bằng cách nhấn nút Run (màu xanh lá cây).

📌 4. Kiểm tra (Health Check)
Sau khi app đã chạy (thường là port 8080), bạn có thể kiểm tra xem mọi thứ đã ổn chưa:
Check Database: Mở MySQL Workbench hoặc DBeaver, kiểm tra xem database rubysparks_db đã được tự động tạo kèm các bảng (Tables) hay chưa.