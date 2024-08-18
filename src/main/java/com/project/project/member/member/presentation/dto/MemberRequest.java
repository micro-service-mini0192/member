package com.project.project.member.member.presentation.dto;

import com.project.project.member.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

public class MemberRequest {
    @Builder
    @Schema(name = "Create member")
    public record Save(
            @NotBlank @Size(min = 5)
            @Schema(example = "user12")
            String username,

            @NotBlank
            @Schema(example = "mini")
            String nickname,

            @NotBlank @Size(min = 1) @Email
            @Schema(example = "example@naver.com")
            String email,

            @NotBlank @Size(min = 8)
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])\\S+$",
                    message = "Password must contain at least one letter (upper case and lower case) and one number")
            @Schema(example = "1q2w3e4r")
            String password,

            @NotNull
            @Min(1) @Max(120)
            @Schema(example = "90")
            Integer age
    ) {
        static public Member toEntity(Save dto, String password) {
            if(dto.password.equals(password)) throw new RuntimeException("비밀번호 암호화가 진행되지 않았습니다");
            return Member.builder()
                    .username(dto.username())
                    .nickname(dto.nickname())
                    .email(dto.email())
                    .age(dto.age())
                    .password(password)
                    .role(List.of(MemberRole.USER.getRole()))
                    .locked(MemberStatus.FALSE.getStatus())
                    .build();
        }
    }
}
