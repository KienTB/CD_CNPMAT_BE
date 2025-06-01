package VNNet.VNNet.Service;

import VNNet.VNNet.Model.Otp;
import VNNet.VNNet.Repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final long OTP_VALID_DURATION = 5;

    public String generateOTP(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void saveOrUdateOtp(Long userId, String otpCode){
        Otp existingOtp = otpRepository.findByUserId(userId);
        LocalDateTime currentTime = LocalDateTime.now();

        if(existingOtp != null){
            existingOtp.setOtp(otpCode);
            existingOtp.setTimestamp(currentTime); // Trực tiếp lưu LocalDateTime
            otpRepository.save(existingOtp);
        }else{
            Otp newOtp = new Otp();
            newOtp.setUserId(userId);
            newOtp.setOtp(otpCode);
            newOtp.setTimestamp(currentTime); // Trực tiếp lưu LocalDateTime
            otpRepository.save(newOtp);
        }
    }

    public boolean verifyOtp(Long userId, String otpCode){
        Otp storedOtp = otpRepository.findByUserId(userId);
        if(storedOtp == null){
            return false;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime otpCreationTime = storedOtp.getTimestamp(); 

        long minutesPassed = ChronoUnit.MINUTES.between(otpCreationTime, currentTime);
        if(minutesPassed > OTP_VALID_DURATION){
            deleteOtp(userId);
            return false;
        }
        return storedOtp.getOtp().equals(otpCode);
    }

    public void senOtpToEmail(Long userId, String email){
        String otpCode = generateOTP();
        saveOrUdateOtp(userId, otpCode);
        emailService.sendOtpEmail(email, otpCode);
    }

    public void deleteOtp(Long userId){
        Otp otp = otpRepository.findByUserId(userId);
        if(otp != null){
            otpRepository.delete(otp);
        }
    }
}