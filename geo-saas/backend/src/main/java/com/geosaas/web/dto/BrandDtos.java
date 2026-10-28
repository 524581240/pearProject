package com.geosaas.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class BrandDtos {
    @Data
    public static class UpsertRequest {
        @NotBlank(message = "品牌名不能为空")
        private String name;
        private String aliases;
        private String domain;
        private String industry;
        private String locale;
        private String country;
    }

    @Data
    public static class CompetitorRequest {
        @NotBlank
        private String name;
        private String aliases;
        private String domain;
    }
}
