package StudyForge_backend.Security;

import StudyForge_backend.Model.Users;
import StudyForge_backend.Repository.UsersRepository;
import StudyForge_backend.Service.CookieBuilderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UsersRepository usersRepository;
    private final CookieBuilderService cookieBuilderService;
    private final JwtUtils jwtUtils;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException
    {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User AuthUser =  token.getPrincipal();

        String email = AuthUser.getAttribute("email");

        Users user = usersRepository.findByEmail(email);
        if(user == null){
            user = new Users();
            user.setEmail(email);
            user.setUserTokens(20);
            usersRepository.save(user);
        }

        String jwtToken = jwtUtils.generateJwtToken(email, user.getId());

        Cookie cookie = cookieBuilderService.create(jwtToken);

        response.addCookie(cookie);

        response.sendRedirect("http://localhost:3000");

    }
}
