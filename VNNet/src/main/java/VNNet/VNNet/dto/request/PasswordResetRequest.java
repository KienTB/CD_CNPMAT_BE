package VNNet.VNNet.dto.request;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private Long userId;
    private String otp;
    private String newPassword;
}
