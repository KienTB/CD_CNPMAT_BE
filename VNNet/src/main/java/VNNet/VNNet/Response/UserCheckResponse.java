package VNNet.VNNet.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCheckResponse {
    private Long userId;
    private String email;
    private String phoneNumber;
}
