package com.study.diary.repository;

import com.study.diary.entity.DiaryMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<DiaryMember, Long> {
    Optional<DiaryMember> findByloginId(String loginId);

    @Query("select m FROM DiaryMember m where m.loginId = :loginId")
    DiaryMember findMember(String loginId);

    void deleteMemberByLoginId(String loginId);
}