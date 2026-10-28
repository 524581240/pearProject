package com.geosaas.domain.billing;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "subscription")
public class Subscription extends BaseEntity {
    @Column(nullable = false, unique = true)
    private Long tenantId;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false, length = 32)
    private String planCode;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE"; // TRIAL / ACTIVE / EXPIRED / CANCELLED

    @Column(nullable = false, length = 16)
    private String billingCycle = "MONTHLY"; // MONTHLY / YEARLY

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private Integer brandLimit;
    private Integer promptLimit;
    private Integer engineLimit;
    private Integer monthlyResponseLimit;
    private Integer seatLimit;
    private Integer historyDays;
}
