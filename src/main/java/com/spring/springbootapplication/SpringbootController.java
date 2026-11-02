package com.spring.springbootapplication;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.spring.springbootapplication.form.ProfileForm;
import com.spring.springbootapplication.form.SignupForm;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Controller
public class SpringbootController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;
    
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

        ProfileForm profileForm = new ProfileForm();

        // 初期値としての自己紹介文を入れておく変数を、最初は空で用意
        String dbIntroduction = "";

        if (userDetails != null) {
            //「氏名」を表示するために、DBから取得
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user != null) {
                model.addAttribute("username", user.getName());
                if (user.getIntroduction() != null) {
                    dbIntroduction = user.getIntroduction();
                }
            } else {
                model.addAttribute("username", email);
            }
        } else {
            model.addAttribute("username", "ゲスト");
        }

        // キープしていた自己紹介文を、入力欄と画面表示にそれぞれセット
        profileForm.setIntroduction(dbIntroduction);
        model.addAttribute("introduction", dbIntroduction);
        model.addAttribute("imagePath", null);

        // データが詰まった状態のフォームをHTMLへ引き渡す
        model.addAttribute("profileForm", profileForm);

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
        // バリデーションエラーに引っかかった場合の処理
        if (bindingResult.hasErrors()) {
            if (userDetails != null) {
                String email = userDetails.getUsername();
                User user = userRepository.findByEmail(email).orElse(null);
                
                if (user != null) {
                    model.addAttribute("username", user.getName());
                    model.addAttribute("introduction", user.getIntroduction());
                    model.addAttribute("imagePath", user.getImagePath());
                }
            }
            return "profile-edit";
        }

        // チェックOKなら、DBに正しく保存する
        if (userDetails != null) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user != null) {
                user.setIntroduction(profileForm.getIntroduction());
                
                // 画像ファイルが選択されている場合の処理
                if (profileForm.getImageFile() != null && !profileForm.getImageFile().isEmpty()) {
                    try {
                        // 1. Cloudinaryのクライアントを初期化
                        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                            "cloud_name", cloudName,
                            "api_key", apiKey,
                            "api_secret", apiSecret,
                            "secure", true
                        ));

                        // 2. 画像ファイルをCloudinaryにアップロード
                        Map uploadResult = cloudinary.uploader().upload(
                            profileForm.getImageFile().getBytes(),
                            ObjectUtils.emptyMap()
                        );

                        // 3. アップロード成功後に返ってくる「画像のフルURL」を取得
                        String cloudinaryUrl = (String) uploadResult.get("secure_url");
                        
                        // 4. DBにはファイル名ではなく、画像のインターネットURLを保存する
                        user.setImagePath(cloudinaryUrl);
        
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
                // データベースを更新
                userRepository.save(user);
            }
        }

        // 登録後はTOP画面に正常に遷移する
        return "redirect:/top";
    }
}