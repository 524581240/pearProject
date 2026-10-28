package com.geosaas.domain.job;

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
@Table(name = "monitor_job")
public class MonitorJob extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long brandId;

    @Column(nullable = false, length = 128)
    private String name;

    /** DAILY / WEEKLY / MANUAL */
    @Column(nullable = false, length = 32)
    private String frequency = "DAILY";

    /** comma separated engine codes */
    @Column(nullable = false, length = 256)
    private String engineCodes;

    /** comma separated prompt ids */
    @Column(nullable = false, length = 1024)
    private String promptIds;

    @Column(nullable = false)
    private Integer repeatCount = 1;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    private LocalDateTime lastRunAt;
    private LocalDateTime nextRunAt;
}
