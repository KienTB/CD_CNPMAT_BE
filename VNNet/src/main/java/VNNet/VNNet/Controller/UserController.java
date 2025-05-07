package VNNet.VNNet.Controller;

import VNNet.VNNet.Response.ApiResponse;
import VNNet.VNNet.Model.User;
import VNNet.VNNet.Repository.UserRepository;
import VNNet.VNNet.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/profile")
    public ResponseEntity<ApiResponse<User>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            ApiResponse<User> response = new ApiResponse<>(false, "User not authenticated", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String phoneNumber = authentication.getName();

            Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                user.setPassword(null);

                ApiResponse<User> response = new ApiResponse<>(true, "User profile retrieved successfully", user);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<User> response = new ApiResponse<>(false, "User not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error retrieving user profile", e);
            ApiResponse<User> response = new ApiResponse<>(false, "Error retrieving user profile", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/parent/id")
    public ResponseEntity<ApiResponse<List<Long>>> getAllParentId(){
        try{
            List<Long> parentId = userRepository.findByRole("parent")
                    .stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Success", parentId)
            );
        }catch(Exception e){
            logger.error("Error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error", null));
        }
    }

    @GetMapping("/user/teacher/id")
    public ResponseEntity<ApiResponse<List<Long>>> getAllTeacherId(){
        try{
            List<Long> teacherId = userRepository.findByRole("teacher")
                    .stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Success", teacherId)
            );
        }catch(Exception e){
            logger.error("Error retrieving teacher ID",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error", null));
        }
    }



}

