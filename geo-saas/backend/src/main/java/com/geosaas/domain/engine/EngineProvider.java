package com.geosaas.domain.engine;

import com.geosaas.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "engine_provider")
public class EngineProvider extends BaseEntity {
    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(length = 32)
    private String region = "GLOBAL"; // GLOBAL / CN

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(length = 32)
    private String adapterType = "MOCK"; // MOCK / HTTP

    @Column(length = 512)
    private String endpoint;

    /** encrypted or plain for demo */
    @Column(length = 512)
    private String apiKey;
}
