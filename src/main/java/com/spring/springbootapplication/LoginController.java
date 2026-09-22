package com.spring.springbootapplication;

import java.util.regex.Pattern;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        @PostMapping("/login")
    public String loginValidation(
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        RedirectAttributes redirectAttributes
    ) {
        // 未入力のチェック
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "メールアドレスを入力してください");
            return "redirect:/login";
        }

        // 入力されている場合のみ、形式チェック (xxx@yyy.zzz) を行う
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(emailPattern, email)) {
            // 正しい形式ではありません。と出すように分岐
            redirectAttributes.addFlashAttribute("errorMessage", "メールアドレスが正しい形式ではありません");
            return "redirect:/login";
        }

        return "forward:/login";
    }
}