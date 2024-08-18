package com.project.project.security;

import com.project.project.config.exception.ServerException;
import com.project.project.member.member.domain.MemberDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUnit {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MemberDetails) {
            return ((MemberDetails) authentication.getPrincipal()).getId();
        }
        throw new ServerException("Unauthenticated user");
    }
}
