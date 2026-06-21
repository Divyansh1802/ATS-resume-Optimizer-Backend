package StudyForge_backend.Service;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieBuilderService {

    public Cookie create(String token){
        Cookie cookie = new Cookie("jwt", token);
        cookie.setPath("/");
        cookie.setMaxAge(7*24*60*60);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        return cookie;
    }

}
