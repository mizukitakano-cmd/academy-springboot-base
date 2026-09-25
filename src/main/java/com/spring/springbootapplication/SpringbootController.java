package com.spring.springbootapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.springbootapplication.form.SignupForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Collections;

@Controller
public class SpringbootController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    //新規登録画面の表示
    @GetMapping("/signin")
    public String showSignupPage(Model model) {
        model.addAttribute("springbootForm", new SignupForm());
        return "signin"; // signin.htmlを表示
    }

    //新規登録の処理
    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("springbootForm") SignupForm signupForm,
        BindingResult result,
        Model model,
        HttpServletRequest request
    ) {
        if (userRepository.existsByEmail(signupForm.getEmail())){
            result.rejectValue("email", "error.email", "このメールアドレスは既に登録されています。");
        }

        if (result.hasErrors()) {
            return "signin";
        }

        String rawPassword = signupForm.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setName(signupForm.getName());
        user.setEmail(signupForm.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // 新規登録後に自動ログイン状態にする処理
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/signin";
        }

        return "redirect:/top";
    }

    //ログイン画面の表示
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        
        // 要件：メールアドレス、パスワードが一致しない場合はエラーメッセージを表示させる
        if (error != null) {
            model.addAttribute("loginError", "メールアドレス、もしくはパスワードが間違っています");
        }
        
        return "login"; // login.htmlを表示
    }

    //TOPページの表示
    @GetMapping("/top")
    public String showTopPage() {
        return "top";
    }
}