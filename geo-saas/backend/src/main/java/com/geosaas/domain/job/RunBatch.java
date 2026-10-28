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
@Table(name = "run_batch")
public class RunBatch extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long jobId;

    @Column(nullable = false, length = 32)
    private String triggerType; // SCHEDULE / MANUAL

    @Column(nullable = false, length = 32)
    private String status = "RUNNING"; // RUNNING / SUCCESS / PARTIAL / FAILED

    private Integer totalItems = 0;
    private Integer successItems = 0;
    private Integer failedItems = 0;

    private LocalDateTime finishedAt;
}
