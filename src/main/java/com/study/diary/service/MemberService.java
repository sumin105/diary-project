package com.study.diary.service;

import com.study.diary.entity.Diary;
import com.study.diary.entity.DiaryMember;
import com.study.diary.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.UUID;


import com.study.diary.service.DataNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    @Autowired
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public DiaryMember create(String loginId, String name, String email, String password) {
        DiaryMember member = new DiaryMember();
        member.setLoginId(loginId);
        member.setName(name);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        this.memberRepository.save(member);

        return member;
    }

    public DiaryMember getMember(String loginId) {
        Optional<DiaryMember> diaryMember = this.memberRepository.findByloginId(loginId);
        if (diaryMember.isPresent()) {
            return diaryMember.get();
        } else {
            throw new DataNotFoundException("diarymember not found");
        }
    }

    public DiaryMember getProfile(String loginId) {
        return this.memberRepository.findMember(loginId);
    }

    public void removeProfile(String loginId) throws Exception {
        DiaryMember member = this.memberRepository.findByloginId(loginId)
                .orElseThrow(() -> new DataNotFoundException("DiaryMember not found: " + loginId));
        String projectPath = System.getProperty("user.dir") +
                "/src/main/resources/static/files/member";
        File existingProfileImage = new File(projectPath, member.getProfilename());

        if (existingProfileImage.exists()) {
            existingProfileImage.delete();
        }

        member.setProfilename(null);
        member.setProfilepath(null);
        this.memberRepository.save(member);
    }

    public void profileModify(String loginId, MultipartFile file) throws Exception {
        DiaryMember member = this.memberRepository.findByloginId(loginId)
                .orElseThrow(() -> new DataNotFoundException("DiaryMember not found: " + loginId));

        if (member.getProfilename() != null) {
            String projectPath = System.getProperty("user.dir") +
                    "/src/main/resources/static/files/member";
            File existingProfileImage = new File(projectPath, member.getProfilename());

            if (existingProfileImage.exists()) {
                existingProfileImage.delete();
            }

        }

        String projectPath = System.getProperty("user.dir") +
                "/src/main/resources/static/files/member";
        UUID uuid = UUID.randomUUID();
        String filename = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(projectPath, filename);
        file.transferTo(saveFile);

        member.setLoginId(loginId);
        member.setProfilename(filename);
        member.setProfilepath("/files/member/"+filename);

        this.memberRepository.save(member);
    }

    @Transactional
    public void deleteMemberByLoginId(String loginId){
        memberRepository.deleteMemberByLoginId(loginId);
    }
}
