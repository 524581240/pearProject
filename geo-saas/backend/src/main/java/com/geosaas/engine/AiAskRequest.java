package com.geosaas.engine;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiAskRequest {
    private String engineCode;
    private String prompt;
    private String brandName;
    private String brandAliases;
    private String competitors;
    private String locale;
    private String country;
}
