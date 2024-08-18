package com.project.project.member.member.application;

import com.project.project.member.member.presentation.dto.MemberRequest;

public interface MemberService {
    void save(MemberRequest.Save takenDto);
}
