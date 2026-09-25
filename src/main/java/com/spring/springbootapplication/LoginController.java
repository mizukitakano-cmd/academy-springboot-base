package com.spring.springbootapplication;

import java.util.regex.Pattern;
import java.util.Collections;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

  // DBからユーザーを探す
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        // ユーザーが存在しない、またはパスワードが一致しない場合
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            redirectAttributes.addFlashAttribute("loginError", "メールアドレス、もしくはパスワードが間違っています");
            return "redirect:/login";
        }

        //すべてのチェックに合格/top へジャンプ ───
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