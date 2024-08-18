package com.project.project.member.member.presentation;

import com.project.project.member.member.application.MemberService;
import com.project.project.member.member.presentation.dto.MemberRequest;
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
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@Tag(name = "Member API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "Join", description = "Create member")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Creation completed"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<String> save(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                       @Valid @RequestBody MemberRequest.Save takenDto,
                                       HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("{}: “Create Member” API call", ip);

        memberService.save(takenDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Creation completed");
    }

    @GetMapping
    @Operation(summary = "Join", description = "Create member")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Creation completed"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<String> test(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("{}: “Create Member” API call", ip);

        return ResponseEntity.status(HttpStatus.CREATED).body("Creation completed");
    }
}
