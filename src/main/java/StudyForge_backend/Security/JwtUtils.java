package StudyForge_backend.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    private final long expiration = 1000L*60*60*24*7;

    public String generateJwtToken(String email,Long id){
        return Jwts.builder()
                .subject(email)
                .claim("id",id)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration))
                .signWith(custom_key(),Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey custom_key(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith(custom_key())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }


    public boolean validateJwtToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(custom_key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch(Exception e){
            System.out.println("INVALID TOKEN\n"+e.getMessage());
            return false;
        }
    }

}
