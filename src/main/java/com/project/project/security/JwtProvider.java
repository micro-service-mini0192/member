package com.project.project.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.project.member.member.domain.Member;
import com.project.project.member.member.domain.MemberDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;

@Configuration
public class JwtProvider {
    @Value("${JWTKey}")
    public String SECRET;

    //JWT token is valid for one day
    public final int JWT_EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    //Refresh token is valid for 15 days
    public final int REFRESH_EXPIRATION_TIME = 15 * 24 * 60 * 60 * 1000;

    //JWT tokens can be reissued 10 times
    public final Integer REFRESH_COUNT = 10;

    public final String TOKEN_PREFIX = "Bearer ";
    public final String JWT_HEADER_STRING = "Authorization";
    public final String REFRESH_HEADER_STRING = "Refresh";


    // The JWT token has user ID and role information
    public String createJwtToken(MemberDetails member) {
        String[] role = member.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);

        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .withClaim("id", member.getId())
                .withArrayClaim("role", role)
                .sign(Algorithm.HMAC512(SECRET));

    }

    // The Refresh token has user ID and role information
    public String createRefreshToken(MemberDetails member, long createTime) {
        String[] role = member.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);

        return JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .withClaim("id", member.getId())
                .withArrayClaim("role", role)
                .sign(Algorithm.HMAC512(String.valueOf(createTime + REFRESH_EXPIRATION_TIME)));

    }

    public Member decodeToken(String token, String key) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(key))
                .build()
                .verify(token);

        Long id = decodedJWT.getClaim("id").asLong();
        List<String> role = decodedJWT.getClaim("role").asList(String.class);

        return Member.builder()
                .id(id)
                .role(role)
                .build();
    }
}
