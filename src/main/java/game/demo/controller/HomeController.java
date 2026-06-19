package game.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** Vue SPA 入口：根路径返回 index.html */
    @GetMapping("/")
    public String rootPage() {
        return "forward:/index.html";
    }

    /**
     * Vue Router History 模式：非 API/静态资源的前端路由统一回退到 index.html
     */
    @GetMapping({
            "/home",
            "/about",
            "/service",
            "/subscription",
            "/user-center",
            "/contact",
            "/admin-dashboard",
            "/admin-games",
            "/admin-feedback"
    })
    public String spaRoutes() {
        return "forward:/index.html";
    }
}
