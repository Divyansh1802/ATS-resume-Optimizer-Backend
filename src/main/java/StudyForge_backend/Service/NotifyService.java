package StudyForge_backend.Service;

import StudyForge_backend.Model.Users;
import StudyForge_backend.Repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class NotifyService {
    private final UsersRepository usersRepository;
    private final JavaMailSender javaMailSender;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String sender;

    @Transactional
    public void SendOtp(String email) {
        try{
            Users user = usersRepository.findByEmail(email);
            if(user == null){
                user = new Users();
                user.setEmail(email);
            }

            SimpleMailMessage message = new SimpleMailMessage();

            String otp = generateOTP(email);

            message.setText("""
                This is a system generated mail. Please do not
                reply to this Email ID. If you have a query or need any clarification you may: \n
                Your request to login to user profile  has been received.\n
                Please submit Email Id Verification OTP to process this request.\n
                """ + "OTP: " + otp);
            message.setTo(email);
            message.setSubject("STUDYFORGE - Notify Service");
            message.setFrom(sender);

            javaMailSender.send(message);

            user.setOtp(passwordEncoder.encode(otp));
            user.setOtpSentAt(LocalDateTime.now());
            usersRepository.save(user);

            System.out.println("Your OTP has been sent.");
        }
        catch (Exception e){
            throw new RuntimeException("Error sending OTP");
        }
    }

    public  String generateOTP(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());

        int num = Math.abs(java.nio.ByteBuffer.wrap(hash).getInt());
        return String.format("%06d", num % 1_000_000);
    }

}
