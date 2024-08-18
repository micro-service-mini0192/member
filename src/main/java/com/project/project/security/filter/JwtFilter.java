package com.project.project.security.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.project.project.member.member.domain.Member;
import com.project.project.member.member.domain.MemberDetails;
import com.project.project.security.JwtProvider;
import com.project.project.security.refreshtoken.RefreshToken;
import com.project.project.security.refreshtoken.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        /*
        1. JWT token reissue
        If header has Refresh token
        Step 1: If Refresh token is null, return 401
        Step 2: If Refresh token is not null, JWT reissue
        */
        String refreshTokenHeader = request.getHeader(jwtProvider.REFRESH_HEADER_STRING);
        if(refreshTokenHeader != null) {
            log.info("JWT Token 재발급");
            refreshTokenHeader = refreshTokenHeader.replace(jwtProvider.TOKEN_PREFIX, "");

            RefreshToken savedRefreshToken = refreshTokenService.findById(refreshTokenHeader);
            if(savedRefreshToken == null) {
                log.info("Refresh token is invalid");
                response.setStatus(401);
                return;
            }

            Member tokenMember = jwtProvider.decodeToken(refreshTokenHeader, String.valueOf(savedRefreshToken.getExpirationTime()));

            log.info("JWT token has been reissued");
            MemberDetails reissueTokenMemberDetails = new MemberDetails(tokenMember);
            String reissueToken = jwtProvider.createJwtToken(reissueTokenMemberDetails);
            response.addHeader(jwtProvider.JWT_HEADER_STRING, jwtProvider.TOKEN_PREFIX + reissueToken);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(reissueTokenMemberDetails, null, reissueTokenMemberDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
            return;
        }

        /*
        Check JWT Token
        If header has JWT token
        Step 1: Attempt to decrypt JWT token
        Step 2: If decrypt is fails, return 401 or 400
         */
        String jwtTokenHeader = request.getHeader(jwtProvider.JWT_HEADER_STRING);
        if(jwtTokenHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtTokenHeader = jwtTokenHeader.replace(jwtProvider.TOKEN_PREFIX, "");

        try {
            Member jwtTokenMember = jwtProvider.decodeToken(jwtTokenHeader, jwtProvider.SECRET);
            MemberDetails jwtTokenMemberDetails = new MemberDetails(jwtTokenMember);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(jwtTokenMemberDetails, null, jwtTokenMemberDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch(TokenExpiredException e) {
            response.setStatus(401);
            log.info("JWT token has expired");

        } catch (JWTDecodeException e) {
            log.info("JWT token is invalid");

        } catch (JWTVerificationException e) {
            log.info("Authentication failed");

        }

        filterChain.doFilter(request, response);
    }
}
