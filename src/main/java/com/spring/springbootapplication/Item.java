package com.spring.springbootapplication;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String category;

    private String name;

    @Column(name = "study_time")
    private Integer studyTime;

    @Column(name = "study_date")
    private LocalDate studyDate;
}