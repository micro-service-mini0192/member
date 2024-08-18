package com.project.project.security.refreshtoken.application;

import com.project.project.security.refreshtoken.domain.RefreshToken;

public interface RefreshTokenService {
    void save(RefreshToken data);
    RefreshToken findById(String token);
    void deleteById(String token);
}
