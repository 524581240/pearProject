package com.geosaas.domain.user;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_account")
public class UserAccount extends BaseEntity {
    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(nullable = false, length = 128)
    private String passwordHash;

    @Column(length = 128)
    private String email;

    @Column(length = 64)
    private String displayName;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 32)
    private String role = "OWNER"; // OWNER / ADMIN / ANALYST / VIEWER / PLATFORM_ADMIN

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
}
