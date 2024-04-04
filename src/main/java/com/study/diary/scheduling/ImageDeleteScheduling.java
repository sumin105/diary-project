package com.study.diary.scheduling;

import com.study.diary.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageDeleteScheduling {

    @Autowired
    private DiaryService diaryService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행("0 0 0 * * *")
    public void DeleteImages() {
        System.out.println("start scheduling");
        List<String> databaseImageNameList = diaryService.imageNameList();
        for (String name : databaseImageNameList) {
            System.out.println(name);
        }

        List<String> serverImageNameList = getImageNames();

        for (String serverImageName : serverImageNameList) {
            if (!databaseImageNameList.contains(serverImageName)) {
                System.out.println("Image not found in database: " + serverImageName);
                deleteImageFile(serverImageName);
            }
        }
    }

    private List<String> getImageNames() {
        // 이미지 파일이 저장된 디렉토리 경로
        String directoryPath = "src/main/resources/static/files/diary";
        List<String> imageNameList = new ArrayList<>();

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        // 파일 목록이 비어있지 않은 경우
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    imageNameList.add(file.getName());
                }
            }
        } else {
            // 파일 목록이 비어있는 경우
            System.out.println("No files found in the directory: " + directory.getAbsolutePath());
        }
        return imageNameList;
    }

    private void deleteImageFile(String fileName) {
        try {
            String directoryPath = "src/main/resources/static/files/diary";
            File file = new File(directoryPath, fileName);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Deleted file: " + fileName);
                } else {
                    System.out.println("Failed to delete file: " + fileName);
                }
            } else {
                System.out.println("File does not exist: " + fileName);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting file: " + fileName);
            e.printStackTrace();
        }
    }
}
