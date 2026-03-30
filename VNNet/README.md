# VNNet (School Management System)

## 1. Introduction
- **Mô tả ngắn gọn về dự án**: VNNet là một hệ thống backend API quản lý trường học, giúp số hóa quá trình tương tác giữa nhà trường, giáo viên, phụ huynh và học sinh.
- **Bài toán dự án giải quyết**: Cung cấp giải pháp số hóa đối với sổ liên lạc và quản lý thông tin nhà trường. Việc này hỗ trợ nhà trường và phụ huynh theo dõi điểm số, thời khóa biểu, nhận thông báo đẩy liên tục giúp kết nối liên lạc liền mạch.
- **Đối tượng sử dụng**: Admin (Quản trị viên nhà trường), Teacher (Giáo viên), Parent (Phụ huynh), và Student (Học sinh).

## 2. Tech Stack
- **Backend:** Java 21, Spring Boot 3.3.3, Spring Data JPA, Spring Security (JWT), Spring Mail, Lombok.
- **Frontend:** API RESTful Backend (Không bao gồm giao diện người dùng UI trong repo này).
- **Database:** MySQL Server (Sử dụng Hibernate dialect), tương thích với H2 cho Runtime.
- **Other Tools:** Gradle, SMTP server (tích hợp Gmail cho OTP token/Reset password).

## 3. System Architecture
- **Kiến trúc**: Layered Architecture (Kiến trúc phân tầng Controller -> Service -> Repository) được ứng dụng xây dựng trên RESTful API.
- **Bảo mật (Security Model)**: 
  - Ứng dụng triển khai bảo mật không trạng thái (Stateless Authentication) dựa trên JSON Web Token (JWT), ngăn chặn giả mạo thông qua việc bắt buộc truyền token mỗi khi thực thi API. 
  - Rate Limiter Pattern được nhúng qua Config để giúp chống hiện tượng spam (Brute Force/DDoS) trong các endpoint nhạy cảm (như API cấp OTP).

## 4. Main Features
- **Hệ thống phân quyền**: Xác thực và phân quyền riêng biệt cho các cấp độ user và tác vụ khác nhau: Admin, Teacher, Parent, Student.
- **Authentication**: Xác thực người dùng nâng cao (Đăng nhập, bảo mật JWT).
- **Quản lý học sinh - phụ huynh**: Mapping và cấu hình liên kết tài khoản giữa hồ sơ học sinh và tải khoản phụ huynh (parent_student_mapping).
- **Hệ thống điểm số học tập**: Giáo viên có quyền hạn thêm/sửa/xóa bảng điểm. Phụ huynh xem được điểm số của con qua ứng dụng.
- **Thời khóa biểu và lịch trình**: Thiết lập, lấy lịch học cho học sinh cụ thể (Cấp quyền đọc cho Giáo viên/Phụ huynh).
- **Quản trị OTP và khôi phục tài khoản**: Hệ thống gửi email OTP (qua dịch vụ Mail Google SMTP) tự động để xác minh danh tính tài khoản và cho phép làm mới lại mật khẩu an toàn.

## 5. API Endpoints
Dưới đây là một số API endpoint quan trọng yếu chia theo các nhóm tính năng (Lưu ý: Header phải đính kèm Access token tại các API bảo vệ):

**Authentication & Security:**
- `POST` `/api/user/login`: Chứng thực đăng nhập, nhận về thông tin và Token JWT.
- `POST` `/api/auth/refresh`: Dùng `refresh_token` cấp lại chuỗi Access Token mới.
- `POST` `/api/otp/send`: Gửi OTP xác thực vào email tài khoản (dùng cho đổi hoặc xác nhận tài khoản).
- `POST` `/api/otp/reset-password`: Reset lại mật khẩu dựa theo chứng thực OTP.

**User (Admin System):**
- `POST` `/api/admin/user/register`: Tạo mới tài khoản vào hệ thống, chỉ định Role.
- `GET` `/api/admin/get/all/users`: Lấy danh sách toàn bộ Users.
- `PUT` `/api/admin/update/user/{userId}`: Chỉnh sửa thông tin tài khoản user.

**Student & Parent Tracking:**
- `GET` `/api/user/profile`: Xem thông tin profile User hiện tại theo token sử dụng.
- `GET` `/api/parent/student/{studentId}`: Học sinh/Phụ huynh dùng ID để tra cứu lịch sử hồ sơ học sinh.
- `GET` `/api/teacher/student/{teacherId}`: Giáo viên tra cứu danh sách các học sinh của bản thân.

**Grade & Schedule:**
- `POST` `/api/teacher/add/grade`: GV Thêm dữ liệu kết quả học tập.
- `GET` `/api/parent/grade/{studentId}`: GET danh sách điểm số của các môn học.
- `POST` `/api/teacher/add/schedule`: GV thêm các lịch khai báo sinh hoạt.
- `GET` `/api/parent/schedule/{studentId}`: GET lịch cá nhân của các em học sinh.

## 6. Database Design
Thiết kế database chính sử dụng Entity Relation với kiến trúc bao quát gồm các bảng sau:
- **`users`**: Bảng trung tâm chứa thông tin tài khoản bao gồm số điện thoại, mật khẩu, email và quy định quyền (Role). Liên kết 1-N tới Entity `Student` (Đối tượng Phụ huynh hoặc Giáo viên).
- **`students`**: Hệ thống hồ sơ sinh viên gồm chi tiết (`name`, `birth_date`, `class`, `gender`). Bảng giữ liên kết (Foreign key) đến `Teacher` chỉ định.
- **`parent_student_mapping`**: Chứa thông tin ghép đôi (map) xác định được quan hệ Phụ huỳnh nào quản lý/theo dõi sinh viên/học sinh nào.
- **`grades`**: Bảng quản lý kết quả học tập (`subject`, `score`, `term`), kết nối tương ứng dựa trên `student_id` và giáo viên vào điểm rèn (qua `user_id`).
- **`schedules`**: Quản lý lịch sinh hoạt/học tập theo cột `activity`, `schedule_date`, `status` cho mỗi `student_id`.
- **`notifications`**: Hệ thống nội dung thông báo chung cho users quản trị bởi User Admin cung cấp title và nội dung theo từng thời điểm.
- *(Bảng phụ trợ)*: Khai báo thêm các bảng `tokens`, `otp`, `refresh_token`, `password_reset_token` phục vụ nhu cầu tạo session tracking ngắn và reset mật khẩu.
