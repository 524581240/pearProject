package com.geosaas.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class BillingDtos {
    @Data
    public static class CreateOrderRequest {
        @NotBlank
        private String planCode;
        private String billingCycle;
    }

    @Data
    public static class PayRequest {
        private String channel; // MOCK / WECHAT / ALIPAY
    }

    @Data
    public static class CallbackRequest {
        @NotBlank
        private String orderNo;
        private String channelTradeNo;
        private String status;
    }
}
