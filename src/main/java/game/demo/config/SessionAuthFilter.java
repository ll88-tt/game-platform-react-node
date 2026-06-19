package game.demo.config;

import game.demo.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 将 HttpSession 中的 currentUser 同步到 Spring Security 上下文。
 * 本项目使用自定义登录（非 formLogin），否则已登录用户访问需 authenticated() 的接口会收到 403。
 */
@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("currentUser");
                if (user != null) {
                    String role = user.getRole() != null ? user.getRole() : "ROLE_USER";
                    var authorities = List.of(new SimpleGrantedAuthority(role));
                    var authentication = new UsernamePasswordAuthenticationToken(
                            user.getUsername(), null, authorities);
                    authentication.setDetails(user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
