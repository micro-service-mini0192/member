package com.project.project.security.refreshtoken;

public interface RefreshTokenService {
    void save(RefreshToken data);
    RefreshToken findById(String token);
    void deleteById(String token);
}
