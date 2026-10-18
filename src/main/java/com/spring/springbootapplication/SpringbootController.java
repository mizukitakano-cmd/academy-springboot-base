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
    @GetMapping("/profile-edit")
    public String showProfileEditPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            //「氏名」を表示するために、DBから取得
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                model.addAttribute("username", user.getName());
            } else {
                model.addAttribute("username", email);
            }
        } else {
            model.addAttribute("username", "ゲスト");
        }

        // 自己紹介や画像は空で良いため、受け皿用の空データを設定
        model.addAttribute("introduction", "");
        model.addAttribute("imagePath", null);

        // バリデーションエラーを正常に動かすため、空のフォームオブジェクトを引き渡す
        model.addAttribute("profileForm", new com.spring.springbootapplication.form.ProfileForm());

        // templates/profile-edit.html を表示
        return "profile-edit";
    }

    @PostMapping("/profile-edit")
    public String executeProfileEdit(
        @org.springframework.validation.annotation.Validated @org.springframework.web.bind.annotation.ModelAttribute("profileForm") com.spring.springbootapplication.form.ProfileForm profileForm,
        org.springframework.validation.BindingResult bindingResult,
        @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
        Model model
    ) {
        // バリデーションチェック（空チェック、50〜200文字チェック）
        if (bindingResult.hasErrors()) {
            // エラーがある場合は、名前情報を再度セットして、編集画面（profile-edit）に戻す
            if (userDetails != null) {
                String email = userDetails.getUsername();
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    model.addAttribute("username", user.getName());
                }
            }
            return "profile-edit"; // エラーメッセージを伴って画面に戻る
        }

        // 2. チェックOKなら、DBに正しく保存する
        if (userDetails != null) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user != null) {
                // フォームに入力された自己紹介テキストをDBのユーザーレコードにセット
                user.setIntroduction(profileForm.getIntroduction());
                
                // ※画像ファイルの保存処理
                // if (!profileForm.getImageFile().isEmpty()) { ... }

                // データベースを更新
                userRepository.save(user);
            }
        }

        // 登録後はTOP画面に遷移する
        return "redirect:/top";
    }
}