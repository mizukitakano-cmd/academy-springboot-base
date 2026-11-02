package com.spring.springbootapplication.form;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileForm {

    // 50文字以上200文字以下
    @Size(min = 50, max = 200, message = "自己紹介は50文字以上200文字以下で入力してください")
    private String introduction;

    // 画像ファイルを受け取るためのフィールド
    private MultipartFile imageFile;

    // --- ゲッターとセッター ---

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}