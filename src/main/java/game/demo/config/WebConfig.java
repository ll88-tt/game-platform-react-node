package game.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // SessionInterceptor 已移除，现在使用 Spring Session + Cookie 管理会话
    // 所有会话验证由 Spring Security 和 Spring Session 自动处理

}
