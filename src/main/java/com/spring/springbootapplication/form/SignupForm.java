package com.spring.springbootapplication.form;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    //バリデーションを実行する順番の定義
    public interface Group1 {} // 1番目：空欄（未入力）チェックのグループ
    public interface Group2 {} // 2番目：形式や文字数のチェックのグループ

    //Group1 → Group2 の順番でチェックを走らせるための魔法の指示書
    @GroupSequence({Group1.class, Group2.class})
    public interface GroupOrder {}

    // 氏名のバリデーション
    @NotBlank(message = "氏名は必ず入力してください", groups = Group1.class)
    @Size(max = 255, message = "氏名は255文字以内で入力してください", groups = Group2.class)
    private String name;

    //メールアドレスのバリデーション
    // 未入力時は、groups = Group1.class のこのメッセージだけを動かします
    @NotBlank(message = "メールアドレスを入力してください", groups = Group1.class)
    //形式エラーの時は、groups = Group2.class のこのメッセージが動きます
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "メールアドレスが正しい形式ではありません", groups = Group2.class)
    private String email;

    // パスワードのバリデーション
    @NotBlank(message = "パスワードは必ず入力してください", groups = Group1.class)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "英数字8文字以上で入力してください", groups = Group2.class)
    private String password;
}