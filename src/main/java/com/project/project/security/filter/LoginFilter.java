package com.project.project.security.filter;

import com.project.project.member.member.domain.MemberDetails;
import com.project.project.security.JwtProvider;
import com.project.project.security.refreshtoken.domain.RefreshToken;
import com.project.project.security.refreshtoken.application.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        if(username == null) {
            log.error("Username is empty");
            return null;
        }

        String password = obtainPassword(request);
        if(password == null) {
            log.error("Password is empty");
            return null;
        }

        log.error("Login attempt: {}", username);
        Authentication token = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(token);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.error("Login successful");
        MemberDetails member = (MemberDetails) authResult.getPrincipal();

        long loginTime = System.currentTimeMillis();

        String jwtToken = jwtProvider.createJwtToken(member);
        String refreshToken = jwtProvider.createRefreshToken(member, loginTime);

        RefreshToken saveToken = RefreshToken.builder()
                .id(refreshToken)
                .expirationTime(loginTime + jwtProvider.REFRESH_EXPIRATION_TIME)
                .count(jwtProvider.REFRESH_COUNT)
                .ttl(jwtProvider.REFRESH_EXPIRATION_TIME / 1000)
                .build();

        refreshTokenService.save(saveToken);

        response.addHeader(jwtProvider.JWT_HEADER_STRING, jwtProvider.TOKEN_PREFIX + jwtToken);
        response.addHeader(jwtProvider.REFRESH_HEADER_STRING, jwtProvider.TOKEN_PREFIX + refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        log.error(failed.getMessage());
        response.setStatus(401);

    }
}