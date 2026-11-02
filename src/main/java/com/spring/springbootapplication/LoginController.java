package com.spring.springbootapplication;

import java.util.Collections;
import java.util.regex.Pattern;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class LoginController {

    @org.springframework.beans.factory.annotation.Autowired private UserRepository userRepository;

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
        RedirectAttributes redirectAttributes,
        jakarta.servlet.http.HttpServletRequest request
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
            UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(email, "", Collections.emptyList());

            // 2. 認証トークンを作成
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Collections.emptyList()
            );
            
            // 3. セキュリティコンテキストに設定
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 4. 【最重要】作成した認証情報を, ブラウザのセッションに明示的に永続化（保存）します
            request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/login";
        }

        return "redirect:/top";
    }

    //TOPページの表示
    @GetMapping("/top")
    public String showTopPage(
        @org.springframework.security.core.annotation.AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        // 初期値としての自己紹介文と画像URLを用意
        String dbIntroduction = "";
        String finalImagePath = null;

        if (userDetails != null) {
            String email = userDetails.getUsername();
            // DBからユーザーを探す（なければ null）
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user == null) {
                // 未知のアドレスの場合、DBの必須項目をすべて埋めて自動保存する
                user = new User();
                user.setEmail(email);
                user.setName("新規ユーザー（" + email + "）");
                user.setPassword("dummy_password");
                user.setIntroduction("");
                
                // 必須項目が埋まっていれば、saveしても500エラーになりません
                user = userRepository.save(user);
            }

            // 本物のユーザー、またはその場で作られた擬似ユーザーからデータを取得
            model.addAttribute("username", user.getName());
            
            if (user.getIntroduction() != null) {
                dbIntroduction = user.getIntroduction();
            }

            if (user.getImagePath() != null) {
                finalImagePath = user.getImagePath();
            }
            
        } else {
            model.addAttribute("username", "ゲスト");
        }

        // top.html へに引き渡す
        model.addAttribute("introduction", dbIntroduction);
        model.addAttribute("imagePath", finalImagePath); // これで確実にHTMLへ届きます

        return "top";
    }
}