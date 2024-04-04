package com.study.diary.controller;

import com.study.diary.dto.MemberCreateForm;
import com.study.diary.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup(MemberCreateForm memberCreateForm, Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberCreateForm memberCreateForm,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "패스워드가 일치하지 않습니다.");
            return "signup";
        }

        try {
            memberService.create(memberCreateForm.getLoginId(), memberCreateForm.getName(), memberCreateForm.getEmail(), memberCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        String loginId = principal.getName();
        model.addAttribute("diaryMember", memberService.getProfile(loginId));

        return "profile";
    }

    // 프로필창 사진 변경
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profilepro")
    public String profilePro(Principal principal, MultipartFile file) throws Exception {
        String loginId = principal.getName();

        if (file != null && !file.isEmpty()) {
            memberService.profileModify(loginId, file);
        } else {
            memberService.removeProfile(loginId);
        }
        return "redirect:/member/profile";
    }

    // 계정 탈퇴
    @GetMapping("/leaveaccount")
    public String leaveAccount(Principal principal, HttpServletRequest request,
                               HttpServletResponse response) {
        String loginId = principal.getName();
        memberService.deleteMemberByLoginId(loginId);

        // 세션 무효화 (로그아웃)
        request.getSession().invalidate();

        return "redirect:/";
    }
}
