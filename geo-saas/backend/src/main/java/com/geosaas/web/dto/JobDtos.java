package com.geosaas.web.dto;

import com.geosaas.domain.job.RunBatch;
import com.geosaas.domain.job.RunItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

public class JobDtos {
    @Data
    public static class UpsertRequest {
        @NotNull
        private Long brandId;
        @NotBlank
        private String name;
        private String frequency;
        @NotBlank
        private String engineCodes;
        @NotBlank
        private String promptIds;
        private Integer repeatCount;
    }

    @Data
    public static class VerifyRequest {
        @NotNull
        private Long brandId;
        @NotBlank
        private String engineCodes;
        @NotBlank
        private String promptIds;
        private Integer repeatCount;
    }

    @Data
    public static class BatchDetail {
        private RunBatch batch;
        private List<RunItem> items;
    }
}
