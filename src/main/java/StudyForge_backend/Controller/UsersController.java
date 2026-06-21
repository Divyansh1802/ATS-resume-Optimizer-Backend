package StudyForge_backend.Controller;

import StudyForge_backend.DTO.EmailRequest;
import StudyForge_backend.DTO.VerifyOtpRequest;
import StudyForge_backend.Response.AIResponse;
import StudyForge_backend.Service.CookieBuilderService;
import StudyForge_backend.Service.NotifyService;
import StudyForge_backend.Service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsersController {
    private final NotifyService notifyService;
    private final UsersService  usersService;
    private final CookieBuilderService cookieBuilderService;


    @PostMapping("/otp/sender")
    public ResponseEntity<AIResponse> otpSender(@RequestBody EmailRequest  emailRequest){
        try{
             System.out.println("otp request received");
             notifyService.SendOtp(emailRequest.getEmail());
             return ResponseEntity.ok(new AIResponse("OTP SENT",emailRequest));
        }
        catch(Exception e){
            System.out.println("otp request received error");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AIResponse("failed to send otp",null));
        }
    }

    @PostMapping("/verifyOTP")
    public Boolean verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest , HttpServletResponse response){
        try{
              String token = usersService.verifyOTP(verifyOtpRequest.getEmail(),
                      verifyOtpRequest.getOtp());
              if(token == null){
                  throw new Exception("failed to verify otp");
              }

            Cookie cookie = cookieBuilderService.create(token);
            response.addCookie(cookie);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){

            System.out.println("Request reached logout endpoint");
            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .secure(false)
                    .sameSite("Lax")
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok("Logged out");
    }

    @PostMapping("/getTokens")
    public ResponseEntity<?> GetTokens(HttpServletRequest req){
        try{
            System.out.println("getTokens request received");
            String email = usersService.extractEmail(req);
            usersService.getUserTokens(email);

            return ResponseEntity.ok().body("token retrieved successfully");
        } catch (RuntimeException e) {

            System.err.println("Auth error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {

            System.err.println("Token error: " + e.getMessage());
            String msg = e.getMessage();
            HttpStatus status = msg.contains("token is empty")
                    ? HttpStatus.FORBIDDEN
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(Map.of("error", msg));
        }
    }

}
