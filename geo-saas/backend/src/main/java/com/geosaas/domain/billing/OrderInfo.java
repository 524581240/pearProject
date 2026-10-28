package com.geosaas.domain.billing;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "order_info")
public class OrderInfo extends BaseEntity {
    @Column(nullable = false, unique = true, length = 64)
    private String orderNo;

    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long planId;

    @Column(nullable = false, length = 32)
    private String planCode;

    @Column(nullable = false, length = 16)
    private String billingCycle;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 32)
    private String status = "PENDING"; // PENDING / PAID / CLOSED / REFUNDED

    @Column(length = 32)
    private String payChannel; // MOCK / WECHAT / ALIPAY

    private LocalDateTime paidAt;
}
