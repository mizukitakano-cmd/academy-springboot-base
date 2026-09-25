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

    //ログイン画面を表示する
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        // パスワードが一致しなくてSpring Securityから戻ってきたとき
        if (error != null) {
            model.addAttribute("loginError", "メールアドレス、もしくはパスワードが間違っています");
        }
        return "login";
    }

    //個別のバリデーション
    @PostMapping("/login-validation")
    public String loginValidation(
        @RequestParam("username") String email,
        @RequestParam("password") String password,
        RedirectAttributes redirectAttributes
    ) {
        //未入力のチェック
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("loginError", "メールアドレスを入力してください");
            return "redirect:/login";
        }

        //入力されている場合のみ、形式チェック
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!Pattern.matches(emailPattern, email)) {
            redirectAttributes.addFlashAttribute("loginError", "メールアドレスが正しい形式ではありません");
            return "redirect:/login";
        }

        //パスワードの未入力チェック
        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("loginError", "パスワードを入力してください");
            return "redirect:/login";
        }

        //すべてのチェックをすり抜けたら本来の処理へフォワード
        return "forward:/login";
    }

    //TOPページの表示
    @GetMapping("/top")
    public String showTopPage() {
        return "top";
    }
}