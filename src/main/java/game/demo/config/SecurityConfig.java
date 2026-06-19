package game.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AdminAuthFilter adminAuthFilter;

    @Autowired
    private SessionAuthFilter sessionAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/register", "/api/check-login", "/api/logout").permitAll()
                        .requestMatchers("/api/games", "/api/games/**").permitAll()
                        .requestMatchers("/api/feedback/**").permitAll()
                        .requestMatchers("/api/membership-plans", "/api/membership-plans/**").permitAll()
                        .requestMatchers("/api/payment/**", "/api/purchase").permitAll()
                        .requestMatchers("/api/subscription", "/api/orders").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/","/index.html", "/home", "/about", "/service", "/subscription", "/user-center", "/contact", "/admin-dashboard", "/admin-games", "/admin-feedback", "/assets/**", "/css/**", "/js/**", "/images/**", "/uploads/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(adminAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

}
