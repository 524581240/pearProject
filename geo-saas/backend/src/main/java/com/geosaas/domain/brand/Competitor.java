package com.geosaas.domain.brand;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "competitor")
public class Competitor extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Long brandId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 512)
    private String aliases;

    @Column(length = 256)
    private String domain;
}
