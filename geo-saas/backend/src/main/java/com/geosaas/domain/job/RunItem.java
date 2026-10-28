package com.geosaas.domain.job;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "run_item")
public class RunItem extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long batchId;

    @Column(nullable = false)
    private Long promptId;

    @Column(nullable = false)
    private Integer promptVersion;

    @Column(nullable = false, length = 32)
    private String engineCode;

    @Column(nullable = false, length = 32)
    private String status = "PENDING"; // PENDING / SUCCESS / FAILED

    @Lob
    private String rawAnswer;

    private Boolean mentioned;
    private Integer positionRank;
    private Double sentimentScore;

    @Lob
    private String citationJson;

    @Lob
    private String competitorMentionsJson;

    @Column(length = 512)
    private String errorMessage;

    private Long latencyMs;
}
