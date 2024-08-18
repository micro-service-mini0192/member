package com.project.project.security.refreshtoken.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
    public record JwtReissue(
            @NotBlank
            String refreshToken
    ){}
}
