package com.project.project.security.refreshtoken.presentation;

import com.project.project.member.member.domain.Member;
import com.project.project.member.member.domain.MemberDetails;
import com.project.project.member.member.presentation.dto.MemberRequest;
import com.project.project.security.JwtProvider;
import com.project.project.security.refreshtoken.application.RefreshTokenService;
import com.project.project.security.refreshtoken.domain.RefreshToken;
import com.project.project.security.refreshtoken.presentation.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/v1/refreshToken")
@Tag(name = "Member API")
public class RefreshTokenController {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @GetMapping
    @Operation(summary = "JWT Token reissue", description = "JWT Token reissue")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "JWT Token reissue"),
                    @ApiResponse(responseCode = "401", description = "Refresh token is invalid")
            }
    )
    public ResponseEntity<String> jwtReissue(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                             @Valid @RequestBody RefreshTokenRequest.JwtReissue takenDto,
                                             HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("{}, JWT Token reissue", ip);

        String refreshToken = takenDto.refreshToken();
        refreshToken = refreshToken.replace(jwtProvider.TOKEN_PREFIX, "");

        RefreshToken savedRefreshToken = refreshTokenService.findById(refreshToken);
        if(savedRefreshToken == null) {
            log.info("Refresh token is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is invalid");
        }

        Member tokenMember = jwtProvider.decodeToken(refreshToken, String.valueOf(savedRefreshToken.getExpirationTime()));

        MemberDetails reissueTokenMemberDetails = new MemberDetails(tokenMember);
        String reissueToken = jwtProvider.createJwtToken(reissueTokenMemberDetails);

        log.info("JWT Token reissue successful");
        return ResponseEntity.ok(jwtProvider.TOKEN_PREFIX + reissueToken);
    }
}
