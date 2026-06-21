package StudyForge_backend.Security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final CorsConfigurationSource corsConfigurationSource;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthTokenFilter authTokenFilter) throws Exception
    {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().authenticated());

        http
                .addFilterBefore(authTokenFilter,
                        UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(oauth -> oauth
                .failureHandler((request, response, exception) -> {
                    log.error("OAUTH2 failure", exception.getMessage());
                })
                .successHandler(oAuthSuccessHandler));

        return http.build();

    }
}
