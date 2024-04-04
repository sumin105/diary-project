package com.study.diary.service;

import com.study.diary.entity.DiaryMember;
import com.study.diary.entity.MemberRole;
import com.study.diary.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Service
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final Logger logger = LoggerFactory.getLogger(MemberRepository.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<DiaryMember> _diaryMember = this.memberRepository.findByloginId(username);
        logger.info("findByloginId() 메서드 호출: loginId={}", username); // 메서드 호출 시점 로그
        if (_diaryMember.isEmpty()) {
            logger.error("사용자를 찾을 수 없습니다."); // 예외ㅣ 발생 시 로그
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        DiaryMember diaryMember = _diaryMember.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(MemberRole.MEMBER.getValue()));
        }
        return new User(diaryMember.getLoginId(), diaryMember.getPassword(), authorities);
    }
}
