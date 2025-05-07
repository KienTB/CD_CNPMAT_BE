package VNNet.VNNet.Request;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private Long userId;
    private String otp;
}
