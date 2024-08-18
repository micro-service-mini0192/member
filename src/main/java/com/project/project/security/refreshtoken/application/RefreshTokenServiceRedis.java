package com.project.project.security.refreshtoken.application;

import com.project.project.security.refreshtoken.domain.RefreshToken;
import com.project.project.security.refreshtoken.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceRedis implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void save(RefreshToken data) {
        refreshTokenRepository.save(data);
    }

    @Override
    public RefreshToken findById(String token) {
        Optional<RefreshToken> savedOptionalRefreshToken = refreshTokenRepository.findById(token);
        if(savedOptionalRefreshToken.isEmpty()) return null;

        RefreshToken savedRefreshToken = savedOptionalRefreshToken.get();
        savedRefreshToken.counting();

        if(savedRefreshToken.getCount() < 0) {
            this.deleteById(token);
            return null;
        }

        refreshTokenRepository.save(savedRefreshToken);
        return savedRefreshToken;
    }

    @Override
    public void deleteById(String token) {
        refreshTokenRepository.deleteById(token);
    }
}
