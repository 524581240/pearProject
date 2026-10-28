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
@Table(name = "brand")
public class Brand extends BaseEntity {
    @Column(nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 128)
    private String name;

    /** comma separated aliases */
    @Column(length = 512)
    private String aliases;

    @Column(length = 256)
    private String domain;

    @Column(length = 64)
    private String industry;

    @Column(length = 16)
    private String locale = "zh-CN";

    @Column(length = 16)
    private String country = "CN";

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";
}
