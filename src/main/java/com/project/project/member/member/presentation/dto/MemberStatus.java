package com.project.project.member.member.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
    TRUE(1),
    FALSE(0);

    private final int status;
}
