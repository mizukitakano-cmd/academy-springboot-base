package com.spring.springbootapplication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // ログイン画面を表示する
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        // Spring Securityが認証に失敗すると、自動的に「/login?error=true」に戻る
        if (error != null) {
            model.addAttribute("errorMessage", "メールアドレス、もしくはパスワードが間違っています");
        }
        return "login"; // templates/login.html を表示
    }
}