package com.spring.springbootapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.spring.springbootapplication.form.SignupForm;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class SpringbootController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 新規登録画面の表示
    @GetMapping("/signin")
    public String showSignupPage(Model model) {
        model.addAttribute("springbootForm", new SignupForm());
        return "signin"; // templates/signin.html を表示
    }

    // 新規登録の処理
    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("springbootForm") SignupForm signupForm,
        BindingResult result,
        Model model,
        HttpServletRequest request
    ) {
        // メールアドレスの重複チェック
        if (userRepository.existsByEmail(signupForm.getEmail())){
            result.rejectValue("email", "error.email", "このメールアドレスは既に登録されています。");
        }

        // バリデーションエラーがある場合は新規登録画面に戻る
        if (result.hasErrors()) {
            return "signin";
        }

        // パスワードのハッシュ化（暗号化）
        String rawPassword = signupForm.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // ユーザー情報の保存
        User user = new User();
        user.setName(signupForm.getName());
        user.setEmail(signupForm.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // 自動ログイン
        try {
            request.login(signupForm.getEmail(), rawPassword);
        } catch (ServletException e) {
            e.printStackTrace();
            return "redirect:/signin";
        }

        return "redirect:/top";
    }

    // プロフィール画面の表示
    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        } else {
            model.addAttribute("username", "ゲスト");
        }

        // 要件：自己紹介や画像は空、チャートは未実装で良いため、受け皿用の空データを設定
        model.addAttribute("introduction", ""); // 自己紹介（空）
        model.addAttribute("imagePath", null);     // 画像（空）

        // templates/profile-edit.html を表示
        return "profile-edit";
    }
}