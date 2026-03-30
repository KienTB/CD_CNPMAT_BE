package VNNet.VNNet.dto.request;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private Long userId;
    private String otp;
}
