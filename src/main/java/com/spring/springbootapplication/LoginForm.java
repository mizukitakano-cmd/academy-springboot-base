package com.spring.springbootapplication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class LoginForm {

    @NotBlank(message = "メールアドレスは必ず入力してください。")
    @Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$",
        message = "メールアドレスが正しい形式ではありません。")
    private String email;

    @NotBlank(message = "パスワードは必ず入力してください。")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "英数字8文字以上で入力してください。")
    private String password;
    
}
