package com.study.diary.service;

import com.study.diary.entity.Diary;
import com.study.diary.entity.DiaryMember;
import com.study.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    public void write(Diary diary) {

        diaryRepository.save(diary);
    }

    public Diary diaryView(String dateStr, String loginId) {
        LocalDate date = LocalDate.parse(dateStr);
        return diaryRepository.findByDate(date, loginId);
    }

    public Page<Diary> diaryList(String loginId, Pageable pageable) {
        return diaryRepository.findDiariesByLoginId(loginId, pageable);
    }

    public Page<Diary> diaryListByMonth(int year, int month, String loginId, Pageable pageable) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        return diaryRepository.findByDateBetweenAndLoginId(startDate, endDate, loginId, pageable);
    }

    // 다이어리 존재 여부 확인
    public Diary checkDate(LocalDate date, String loginId) {
        return diaryRepository.findByDate(date , loginId);
    }

    @Transactional
    public void deleteDiaryByDateAndLoginId(LocalDate date, String loginId) {
        this.diaryRepository.deleteByDateAndLoginId(date, loginId);
    }

    public List<String> getFilePathsByDateAndLoginId(LocalDate date, String loginId) {
        List<String> imagePaths = diaryRepository.findFilePathsByDateAndLoginId(date, loginId);
        return imagePaths;
    }

    public List<String> imageNameList() {
        List<Diary> diaries = diaryRepository.findAll();
        List<String> imageNameList = new ArrayList<>();

        for (Diary diary: diaries) {
            if (diary.getFilename() != null) {
                imageNameList.add(diary.getFilename());
            }
        }

        return imageNameList;
    }
}
