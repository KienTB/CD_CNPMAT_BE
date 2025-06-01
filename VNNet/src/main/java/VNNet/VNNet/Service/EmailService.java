package VNNet.VNNet.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kien14122003@gmail.com");
        message.setTo(to);
        message.setSubject("Mã xác thực OTP");
        message.setText("Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 5 phút.");

        mailSender.send(message);
    }
}
