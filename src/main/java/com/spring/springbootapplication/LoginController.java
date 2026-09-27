package com.spring.springbootapplication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    //ログイン画面を表示する
    @GetMapping("/login")
    public String showLoginPage(
        @RequestParam(value = "error", required = false) String error,
        Model model,
        HttpServletRequest request
    ) {
        // Spring Securityがログイン失敗を検知
        if (error != null) {
            String errorMessage = "メールアドレス、もしくはパスワードが間違っています"; // 基本エラーメッセージ

            //URLのパラメータをチェック
            if ("empty_email".equals(error)) {
                errorMessage = "メールアドレスを入力してください";
            } else if ("invalid_format".equals(error)) {
                errorMessage = "メールアドレスが正しい形式ではありません";
            } else if ("empty_password".equals(error)) {
                errorMessage = "パスワードを入力してください";
            }

            model.addAttribute("loginError", errorMessage);
        }
        return "login";
    }

    //TOPページの表示
    @GetMapping("/top")
    public String showTopPage() {
        return "top";
    }
}