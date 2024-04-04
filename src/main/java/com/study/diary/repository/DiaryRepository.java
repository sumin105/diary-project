package com.study.diary.repository;

import com.study.diary.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
   @Query("SELECT d from Diary d where d.date = :date and d.loginId = :loginId")
   Diary findByDate(@Param("date") LocalDate date, @Param("loginId") String loginId);

   void deleteByDateAndLoginId(LocalDate date, String loginId);

   Page<Diary> findDiariesByLoginId(String loginId, Pageable pageable);

   Page<Diary> findByDateBetweenAndLoginId(LocalDate startDate, LocalDate endDate, String loginId, Pageable pageable);

   @Query("select d.filepath FROM Diary d where d.date = :date and d.loginId = :loginId")
   List<String> findFilePathsByDateAndLoginId(LocalDate date, String loginId);


}
