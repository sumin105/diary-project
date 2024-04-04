package com.study.diary.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
public class DiaryMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer member_idx;

    @Column(name = "login_id")
    private String loginId;

    private String password;

    private String name;

    private String email;

    private String profilename;

    private String profilepath;
}
