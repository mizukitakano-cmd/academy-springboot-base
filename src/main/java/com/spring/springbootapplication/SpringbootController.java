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

import com.spring.springbootapplication.form.ProfileForm;
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
        // バリデーションエラー（入力チェック）に引っかかった場合の処理
        if (bindingResult.hasErrors()) {
            if (userDetails != null) {
                String email = userDetails.getUsername();
                User user = userRepository.findByEmail(email).orElse(null);
                
                if (user != null) {
                    // 画面の ${username} に本人の氏名をセット
                    model.addAttribute("username", user.getName());
                    
                    // エラーで画面に戻る時も、DBから本人の保存済みの自己紹介と画像を確実に渡す（ユーザー指定）
                    model.addAttribute("introduction", user.getIntroduction());
                    model.addAttribute("imagePath", user.getImagePath());
                }
            }
            return "profile-edit"; // エラーメッセージを伴って安全に編集画面に戻る
        }

        // 2. チェックOK（エラーなし）なら、DBに正しく保存する
        if (userDetails != null) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user != null) {
                user.setIntroduction(profileForm.getIntroduction());
                
                // 画像ファイルを保存するサーバー内の物理的な「置き場所（フォルダ）」を決定
                if (profileForm.getImageFile() != null && !profileForm.getImageFile().isEmpty()) {
                    try {
                        String fileName = profileForm.getImageFile().getOriginalFilename();
                        user.setImagePath(fileName);

                        // 画像を格納するプロジェクト内の static/images/ フォルダの絶対パスを取得
                        String uploadDir = new java.io.File("src/main/resources/static/images/").getAbsolutePath();
                        java.io.File folder = new java.io.File(uploadDir);
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
        
                        // 選んだ画像データを物理ファイルとしてフォルダに書き出す
                        java.io.File uploadFile = new java.io.File(uploadDir + java.io.File.separator + fileName);
                        profileForm.getImageFile().transferTo(uploadFile);
        
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