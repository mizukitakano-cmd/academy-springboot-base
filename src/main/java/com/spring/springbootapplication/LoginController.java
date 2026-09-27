package com.spring.springbootapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    //ログイン画面を表示する
    @GetMapping("/login")
    public String showLoginPage(
        @RequestParam(value = "error", required = false) String error,
        Model model,
        HttpServletRequest request
    ) {
        // Spring Securityがログイン失敗を検知したとき
        if (error != null) {
            HttpSession session = request.getSession(false);
            String errorMessage = "メールアドレス、もしくはパスワードが間違っています";

            if (session != null) {
                // セッションからSpring Securityの実際のエラー原因を取り出す
                Exception ex = (Exception) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                if (ex != null) {
                    String msg = ex.getMessage();
                    // 空欄だった場合や、Spring Securityが検知したエラーに応じてメッセージを出し分け
                    if (msg != null && msg.contains("UserDetailsService returned null")) {
                        errorMessage = "メールアドレスを入力してください";
                    } else if (msg != null && msg.contains("Bad credentials")) {
                        errorMessage = "メールアドレス、もしくはパスワードが間違っています";
                    }
                }
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