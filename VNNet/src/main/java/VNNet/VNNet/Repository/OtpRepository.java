package VNNet.VNNet.Repository;

import VNNet.VNNet.Model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByUserId(Long userId);
}
