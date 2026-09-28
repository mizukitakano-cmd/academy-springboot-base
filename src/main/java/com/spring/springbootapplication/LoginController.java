package com.spring.springbootapplication;

import java.util.Collections;
import java.util.regex.Pattern;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (error != null) {
            model.addAttribute("loginError", "メールアドレス、もしくはパスワードが間違っています");
        }
        return "login";
    }

    //個別バリデーション
    @PostMapping("/login-validation")
    public String loginValidation(
        @RequestParam("username") String email,
        @RequestParam("password") String password,
        RedirectAttributes redirectAttributes
    ) {
        //アドレス未入力のチェック
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("loginError", "メールアドレスを入力してください");
            return "redirect:/login";
        }

        //形式チェック
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

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }

        return "redirect:/top";
    }

    //TOPページの表示
    @GetMapping("/top")
    public String showTopPage() {
        return "top";
    }
}