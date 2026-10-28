package com.geosaas.domain.billing;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usage_counter", uniqueConstraints = @UniqueConstraint(columnNames = {"tenantId", "yearMonth"}))
public class UsageCounter extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    /** yyyy-MM */
    @Column(nullable = false, length = 7)
    private String yearMonth;

    @Column(nullable = false)
    private Integer responseUsed = 0;
}
