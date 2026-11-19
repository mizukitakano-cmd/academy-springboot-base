package com.spring.springbootapplication;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // ユーザーIDと指定した日付に一致するデータを取得
    List<Item> findByUserIdAndStudyDate(Long userId, LocalDate studyDate);

}