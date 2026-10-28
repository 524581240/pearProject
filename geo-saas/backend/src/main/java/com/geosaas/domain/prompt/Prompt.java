package com.geosaas.domain.prompt;

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
@Table(name = "prompt")
public class Prompt extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long brandId;

    @Column(length = 64)
    private String groupName = "默认";

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer currentVersion = 1;

    @Column(length = 16)
    private String locale = "zh-CN";

    @Column(length = 16)
    private String country = "CN";

    @Column(length = 128)
    private String tags;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE"; // ACTIVE / ARCHIVED
}
