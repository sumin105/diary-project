package com.study.diary.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer diary_idx;

    private String loginId;

    private LocalDate date; //일기 작성 날짜

    private String content; //일기 내용

    private String filename;

    private String filepath;

}
