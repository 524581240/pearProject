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
@Table(name = "prompt_version")
public class PromptVersion extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long promptId;

    @Column(nullable = false)
    private Integer versionNo;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 256)
    private String changeNote;
}
