package com.project.project.member.member.application;

import com.project.project.config.util.methodtimer.MethodTimer;
import com.project.project.member.member.domain.Member;
import com.project.project.member.member.domain.MemberRepository;
import com.project.project.member.member.presentation.dto.MemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @MethodTimer(description = "Save Method")
    public void save(MemberRequest.Save takenDto) {
        String encodingPassword = passwordEncoder.encode(takenDto.password());
        Member takenMember = MemberRequest.Save.toEntity(takenDto, encodingPassword);
        memberRepository.save(takenMember);
    }
}
