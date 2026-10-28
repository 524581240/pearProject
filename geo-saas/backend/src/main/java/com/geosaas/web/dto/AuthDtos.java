package com.geosaas.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDtos {
    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        private String email;
        private String displayName;
        private String companyName;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    public static class TokenResponse {
        private String token;
        private Long userId;
        private Long tenantId;
        private String username;
        private String role;
    }

    @Data
    public static class MeResponse {
        private Long userId;
        private String username;
        private String displayName;
        private String email;
        private String role;
        private Long tenantId;
        private String tenantName;
        private String tenantCode;
    }
}
