package com.spring.springbootapplication;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.spring.springbootapplication.form.SignupForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@Controller
public class SpringbootController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signin")
    public String showSignupPage(Model model) {
        model.addAttribute("springbootForm", new SignupForm());
        return "signin";
    }

    @PostMapping("/register")
    public String registerUser(
        @Valid @ModelAttribute("springbootForm") SignupForm signupForm,
        BindingResult result,
        Model model,
        HttpServletRequest request
    ) {

        if (userRepository.existsByEmail(signupForm.getEmail())){
            result.rejectValue("email","error.email", "このメールアドレスは既に登録されいます。");
        }

        if (result.hasErrors()) {
            model.addAttribute("springbootForm", signupForm);
            return "signin";
        }

        String rawPassword = signupForm.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setName(signupForm.getName());
        user.setEmail(signupForm.getEmail());
        user.setPassword(hashedPassword);

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/top")
    public String showTopPage() {
        return "top";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(
        @jakarta.validation.Valid @ModelAttribute LoginForm loginForm,
        org.springframework.validation.BindingResult result
    ) {
        if (result.hasErrors()) {
            return "login";
        }
        return "redirect:/top";
    }

}