package VNNet.VNNet.Controller;

import VNNet.VNNet.Response.ApiResponse;
import VNNet.VNNet.Model.Student;
import VNNet.VNNet.Model.User;
import VNNet.VNNet.Repository.UserRepository;
import VNNet.VNNet.Service.ParentStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/parent")
public class ParentStudentController {
    private static final Logger logger = LoggerFactory.getLogger(ParentStudentController.class);

    @Autowired
    private ParentStudentService parentStudentService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/student/add")
    public ResponseEntity<ApiResponse<Boolean>> addStudentToParent(@RequestBody Map<String, Long> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            ApiResponse<Boolean> response = new ApiResponse<>(false, "Người dùng chưa xác thực", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String phoneNumber = authentication.getName();
            Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Không tìm thấy người dùng", false));
            }

            Long userId = userOptional.get().getUserId();
            Long studentId = request.get("studentId");

            if (studentId == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "ID học sinh không được để trống", false));
            }

            Boolean result = parentStudentService.addStudentToParent(userId, studentId);

            if (result) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Thêm học sinh thành công", true));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(false, "Không thể thêm học sinh. Vui lòng kiểm tra lại thông tin.", false));
            }
        } catch (Exception e) {
            logger.error("Lỗi khi thêm học sinh cho phụ huynh", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Lỗi hệ thống", false));
        }
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<Student>>> getParentStudents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Người dùng chưa xác thực", null));
        }

        try {
            String phoneNumber = authentication.getName();
            Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Không tìm thấy người dùng", null));
            }

            Long userId = userOptional.get().getUserId();
            List<Student> students = parentStudentService.getStudentsByParentId(userId);

            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách học sinh thành công", students));
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách học sinh của phụ huynh", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Lỗi hệ thống", null));
        }
    }
}