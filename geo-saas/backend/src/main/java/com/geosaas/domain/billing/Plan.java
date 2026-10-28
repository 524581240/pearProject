package com.geosaas.domain.billing;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "plan")
public class Plan extends BaseEntity {
    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(length = 256)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceMonthly;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceYearly;

    private Integer brandLimit = 1;
    private Integer promptLimit = 20;
    private Integer engineLimit = 3;
    private Integer monthlyResponseLimit = 1000;
    private Integer seatLimit = 3;
    private Integer historyDays = 30;

    @Column(nullable = false)
    private Boolean enabled = true;

    private Integer sortOrder = 0;
}
