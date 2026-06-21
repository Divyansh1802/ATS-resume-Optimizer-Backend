package StudyForge_backend.Service;

import StudyForge_backend.Model.Users;
import StudyForge_backend.Repository.UsersRepository;
import StudyForge_backend.Security.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;


    @Transactional
    public String verifyOTP(String email, String otp) throws Exception {
        try{
             if(email == null || otp == null){
                 throw new Exception("Email or password null");
             }

             Users user = usersRepository.findByEmail(email);
             if(user == null){
                 throw new Exception("User not found");
             }
             if(!bCryptPasswordEncoder.matches(otp, user.getOtp())){
                 throw new Exception("Wrong OTP");
             }
             user.setUserTokens(20);
             usersRepository.save(user);

             return jwtUtils.generateJwtToken(user.getEmail(), user.getId());

        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public void getUserTokens(String email) throws Exception {
        if(email == null){
            System.out.println("email is null");
            throw new Exception("Email null");
        }
        Users user = usersRepository.findByEmail(email);
        if(user == null){
            throw new Exception("User not found");
        }
        long val = user.getUserTokens();
        if(val <= 0){
            throw new Exception("User token is empty");
        }
        user.setUserTokens(val - 5);
        usersRepository.save(user);
    }

    public String extractEmail(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_ATSresume".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    if (token != null && jwtUtils.validateJwtToken(token)) {
                        return jwtUtils.getUsernameFromJwtToken(token);
                    }
                }
            }
        }
        System.out.println("COOKIE NOT FOUND");
        throw new RuntimeException("JWT cookie missing or invalid");
    }
}
