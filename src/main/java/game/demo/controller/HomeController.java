package game.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * 访问游戏大厅主页（静态HTML）
     * 方式一：重定向到静态资源（推荐）
     */
    @GetMapping("/home")
    public String homePage() {
        // 重定向到 static 目录下的 home.html
        return "redirect:/home.html";
    }
    @GetMapping("/")
    public String rootPage() {
        return "forward:/home.html";
    }

    /**
     * 如果你希望直接通过 / 访问首页，可以加上这个
     * 注意：如果 LoginController 中已经定义了 "/" 的映射，这里可能会冲突。
     * 解决方案：二选一，或调整其中一个的路径。
     */
    // @GetMapping("/")
    // public String rootPage() {
    //     return "redirect:/home.html";
    // }
}