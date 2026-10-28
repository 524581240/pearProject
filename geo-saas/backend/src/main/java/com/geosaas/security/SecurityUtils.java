package com.geosaas.security;

import com.geosaas.common.BizException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static UserPrincipal currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BizException(401, "未登录");
        }
        return principal;
    }

    public static Long tenantId() {
        return currentUser().getTenantId();
    }
}
