package game.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameApplication.class, args);
    }
}

//如果没有反应，关闭8080进程，再次重启
//# 1. 查找占用 8080 端口的进程
//netstat -ano | findstr :8080
//# 2. 强制终止该进程（将 12345 替换为你看到的实际 PID）
//taskkill /F /PID 12345

//net start MySQL95   管理员
//redis-server

//F12 清空并硬刷新
//以管理员身份打开 PowerShell
//mysql -u myuser -p
//切换到 Game 数据库 USE Game;
//Vue
//GameApplication.java 就是 Spring Boot 后端！
// cd D:\FW\Game\frontend 运行 npm run dev（Vite 前端开发服务器）