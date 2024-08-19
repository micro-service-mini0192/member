package com.project.project.security.refreshtoken.presentation.dto;

import lombok.Builder;

public class RefreshTokenResponse {
    @Builder
    public record JwtToken(
            String jwtToken
    ){}
}
