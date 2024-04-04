package com.study.diary.controller;

import com.study.diary.entity.Diary;
import com.study.diary.entity.DiaryMember;
import com.study.diary.service.DiaryService;
import com.study.diary.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


@Controller
public class DiaryController {

    @Autowired
    private DiaryService diaryService;
    @Autowired
    private MemberService memberService;

    @GetMapping("/diary/calendar")
    public String diaryCalendar() {

        return "calendar";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/diary/calendar";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/diary/write/{date}")
    public String diaryWriteForm(@PathVariable("date") String date, Model model) {
        model.addAttribute("date", date);

        return "diarywrite";
    }

    // 다이어리 작성 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/diary/writepro")
    public String dirayWritePro(@RequestParam("date") String dateStr,
                                @RequestParam("content") String content,
                                Principal principal, @RequestParam("file") MultipartFile file,
                                Model model) throws IOException {
        // 클라이언트에서 전송된 날짜 형식을 'yyyy-MM-dd' 형식으로 변환
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-M-d"));

        DiaryMember diaryMember = this.memberService.getMember(principal.getName());
        System.out.println("diaryMember: " + diaryMember);

        String loginId = principal.getName();

        // 파일 선택유무 확인
        if (file != null && !file.isEmpty()) {
            // 파일 있는 경우
            String projectPath = System.getProperty("user.dir") +
                    "/src/main/resources/static/files/diary";
            UUID uuid = UUID.randomUUID();
            String filename = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, filename);
            file.transferTo(saveFile);

            Diary diary = new Diary();
            diary.setLoginId(loginId);
            diary.setDate(date);
            diary.setContent(content);
            diary.setFilename(filename);
            diary.setFilepath("/files/diary/"+filename);
            diaryService.write(diary);
        } else { // 파일 없는 경우
            Diary diary = new Diary();
            diary.setLoginId(loginId);
            diary.setDate(date);
            diary.setContent(content);
            diaryService.write(diary);
        }

        model.addAttribute("message", "오늘 하루도 작성 완료!");
        model.addAttribute("searchUrl", "/diary/calendar");

        return "message";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/diary/view/{date}")
    public String diaryView(Model model, @PathVariable("date") String date, Principal principal) {
        // 클라이언트에서 전송된 날짜 형식을 'yyyy-MM-dd' 형식으로 변환
        LocalDate formattedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String loginId = principal.getName();

        model.addAttribute("diary", diaryService.diaryView(formattedDate.toString(), loginId));
        return "diaryview";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/diary/modify/{date}")
    public String diaryModify(@PathVariable("date") String date, Principal principal, Model model) {
        String loginId = principal.getName();
        model.addAttribute("diary", diaryService.diaryView(date, loginId));
        return "diarymodify";
    }

    // 날짜 확인 요청 처리
    @GetMapping("/diary/checkDate")
    public ResponseEntity<?> checkDate(@RequestParam("date") String dateStr, Principal principal) {
        String loginId = principal.getName();
        // System.out.println("Received checkDate request for date, loginId: " + dateStr + ", " + loginId);
        try {
            LocalDate date = LocalDate.parse(dateStr);
            Diary diary = diaryService.checkDate(date, loginId);
            if (diary != null && loginId != null) {
                // 데이터베이스에 해당 날짜에 다이어리가 있는 경우
                // System.out.println("Diary exists for date: " + dateStr);
                return new ResponseEntity<>("exists", HttpStatus.OK);
            } else {
                // 데이터베이스에 해당 날짜 다이어리가 없는 경우
                // System.out.println("Diary does not exists for date: " + dateStr);
                return new ResponseEntity<>("not exists", HttpStatus.OK);
            }
        } catch (DateTimeException e) {
            // 요청된 날짜 형식이 올바르지 않은 경우
            System.out.println("Invalid date format: " + dateStr);
            return new ResponseEntity<>("inavalid date format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // 다른 예외 발생 시
            e.printStackTrace();
            return new ResponseEntity<>("error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/diary/update/{date}")
    public String diaryUpdate(@PathVariable("date") String date, Diary diary, Principal principal,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam(value = "deleteImage", required = false) boolean deleteImage,
                              Model model) {
        String loginId = principal.getName();
        Diary diaryTemp = diaryService.diaryView(date, loginId);
        diaryTemp.setLoginId(loginId);
        diaryTemp.setDate(diary.getDate());
        diaryTemp.setContent(diary.getContent());

        // 기존 이미지 삭제 여부 확인
        if (deleteImage) {
            // 기존 이미지 삭제 처리
            if (diaryTemp.getFilename() != null) {
                String projectPath = System.getProperty("user.dir")
                        + "/src/main/resources/static/files/diary";
                File existingFile = new File(projectPath, diaryTemp.getFilename());
                existingFile.delete();
                diaryTemp.setFilename(null);
                diaryTemp.setFilepath(null);
            }
        }

        // 새로운 이미지 업로드 처리
        if (file != null && !file.isEmpty()) {
            try {
                String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files/diary";
                UUID uuid = UUID.randomUUID();
                String filename = uuid + "_" + file.getOriginalFilename();
                File saveFile = new File(projectPath, filename);
                file.transferTo(saveFile);

                // 기존 파일 있을 시 삭제
                if (diaryTemp.getFilename() != null) {
                    File existingFile = new File(projectPath, diaryTemp.getFilename());
                    existingFile.delete();
                }

                diaryTemp.setFilename(filename);
                diaryTemp.setFilepath("/files/diary/" + filename);
            } catch (IOException e) {
                // 파일 저장 중 에러 발생 시 처리
                e.printStackTrace();
            }
        }

        diaryService.write(diaryTemp);

        model.addAttribute("message", "일기 수정 완료!!");
        model.addAttribute("searchUrl", "/diary/calendar");

        return "message";
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    @GetMapping("/diary/delete/{date}")
    public String diaryDelete(@PathVariable("date") LocalDate date, Principal principal) {
        String loginId = principal.getName();

        // 해당 다이어리에 속한 이미지 파일 경로 가져오기
        List<String> imagePaths = diaryService.getFilePathsByDateAndLoginId(date, loginId);
        for (String imagePath : imagePaths) {
            if (imagePath != null) {
                // 서버에서 이미지 파일 삭제
                deleteImageFile(imagePath);
            }
        }

        diaryService.deleteDiaryByDateAndLoginId(date, loginId);
        return "redirect:/";
    }

    private void deleteImageFile(String filePath) {
        System.out.println(filePath);
        String directoryPath = "src/main/resources/static";
        try {
            File file = new File(directoryPath + filePath);
            System.out.println(file);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Deleted file: " + filePath);
                } else {
                    System.out.println("Failed to delete file: " + filePath);
                }
            } else {
                System.out.println("File does not exist: " + filePath);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting file: " + filePath);
            e.printStackTrace();
        }
    }

    // 전체 다이어리 리스트
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/diary/list")
    public String diaryList(@PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable, Principal principal, Model model) {

        String loginId = principal.getName();
        Page<Diary> diaryList = diaryService.diaryList(loginId, pageable);

        int nowPage = diaryList.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, diaryList.getTotalPages());

        model.addAttribute("list", diaryList);
        model.addAttribute("pageTitle", "모든 하루");
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "diarylist";
    }

    // 달 단위 다이어리 리스트
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/diary/list/{year}/{month}")
    public String diaryListByMonth(@PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.ASC) Pageable pageable,
                                   @PathVariable("year") int year,
                                   @PathVariable("month") int month,
                                   Principal principal,
                                   Model model) {
        String loginId = principal.getName();
        Page<Diary> diaryList = diaryService.diaryListByMonth(year, month, loginId, pageable);

        int nowPage = diaryList.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, diaryList.getTotalPages());

        model.addAttribute("list", diaryList);
        model.addAttribute("pageTitle", year + "년 " + month + "월 하루");
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "diarylist";

    }

}
