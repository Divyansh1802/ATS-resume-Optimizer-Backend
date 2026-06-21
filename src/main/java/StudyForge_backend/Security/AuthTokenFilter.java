package StudyForge_backend.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter  extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException
    {
       try{
           Cookie[] cookies = req.getCookies();

           if(cookies != null){
               for(Cookie cookie : cookies){
                   if(cookie.getName().equals("jwt_ATSresume")){

                       String token = cookie.getValue();

                       if (token != null && jwtUtils.validateJwtToken(token)) {
                           String email = jwtUtils.getUsernameFromJwtToken(token);
                           req.setAttribute("email", email);
                           break;
                       }
                   }
               }
           }

           filterChain.doFilter(req, res);
       }
       catch(Exception e){
           System.out.println(e.getMessage());
       }

    }
}
