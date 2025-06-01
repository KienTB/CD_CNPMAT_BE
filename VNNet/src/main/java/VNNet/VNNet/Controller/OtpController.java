package VNNet.VNNet.Controller;

import VNNet.VNNet.Request.OtpRequest;
import VNNet.VNNet.Request.OtpVerificationRequest;
import VNNet.VNNet.Request.PasswordResetRequest;
import VNNet.VNNet.Request.PhoneRequest;
import VNNet.VNNet.Response.ApiResponse;
import VNNet.VNNet.Response.UserCheckResponse;
import VNNet.VNNet.Service.OtpService;
import VNNet.VNNet.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
public class OtpController {
    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/check-phone")
    public ResponseEntity<ApiResponse<UserCheckResponse>> checkPhone(@RequestBody PhoneRequest phoneRequest){
        UserCheckResponse user = userService.findUserByPhoneNumber(phoneRequest.getPhoneNumber());
        if(user != null){
            return ResponseEntity.ok(new ApiResponse<>(true, "Số điện thoại hợp lệ", user));
        }else{
            return ResponseEntity.ok(new ApiResponse<>(false, "Mã OTP không hợp lệ hoặc đã hết hạn", null));
        }
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody OtpRequest otpRequest){
        try {
            otpService.senOtpToEmail(otpRequest.getUserId(), otpRequest.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(true, "Mã OTP đã được gửi vào email", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Không thể gửi mã OTP: " + e.getMessage(), null));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody OtpVerificationRequest otpVerificationRequest){
        boolean isValid = otpService.verifyOtp(
                otpVerificationRequest.getUserId(),
                otpVerificationRequest.getOtp()
        );
        if(isValid){
            return ResponseEntity.ok(new ApiResponse<>(true, "Mã OTP hợp lệ", null));
        }else{
            return ResponseEntity.ok(new ApiResponse<>(false, "Mã OTP không hợp lệ hoặc đã hết hạn", null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest){
        boolean updated = userService.updatePassword(
                passwordResetRequest.getUserId(),
                passwordResetRequest.getNewPassword()
        );
        if(updated){
            otpService.deleteOtp(passwordResetRequest.getUserId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Mật khẩu đã được cập nhật thành công",null));
        }else{
            return ResponseEntity.ok(new ApiResponse<>(false, "Không thể cập nhật mật khẩu", null));
        }
    }
}
