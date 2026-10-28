package com.geosaas.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class PromptDtos {
    @Data
    public static class UpsertRequest {
        @NotNull(message = "品牌不能为空")
        private Long brandId;
        @NotBlank(message = "提示词内容不能为空")
        private String content;
        private String groupName;
        private String tags;
        private String locale;
        private String country;
        private String changeNote;
    }
}
