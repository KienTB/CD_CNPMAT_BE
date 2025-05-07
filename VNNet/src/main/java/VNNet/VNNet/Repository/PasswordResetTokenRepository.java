package VNNet.VNNet.Repository;

import VNNet.VNNet.Model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    void deleteByPhoneNumber(String phoneNumber);
    Optional<PasswordResetToken> findByPhoneNumberAndOtpCode(String phoneNumber, String otpCode);
}
