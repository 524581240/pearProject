package com.geosaas.web;

import com.geosaas.common.ApiResponse;
import com.geosaas.domain.billing.OrderInfo;
import com.geosaas.domain.billing.Plan;
import com.geosaas.service.BillingService;
import com.geosaas.web.dto.BillingDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;

    @GetMapping("/plans")
    public ApiResponse<List<Plan>> plans() {
        return ApiResponse.ok(billingService.listPlans());
    }

    @PostMapping("/orders")
    public ApiResponse<OrderInfo> createOrder(@Valid @RequestBody BillingDtos.CreateOrderRequest req) {
        return ApiResponse.ok(billingService.createOrder(req));
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderInfo>> orders() {
        return ApiResponse.ok(billingService.myOrders());
    }

    @PostMapping("/orders/{id}/pay")
    public ApiResponse<Map<String, Object>> pay(@PathVariable Long id, @RequestBody(required = false) BillingDtos.PayRequest req) {
        if (req == null) req = new BillingDtos.PayRequest();
        return ApiResponse.ok(billingService.pay(id, req));
    }

    @PostMapping("/payments/callback/{channel}")
    public ApiResponse<Void> callback(@PathVariable String channel, @Valid @RequestBody BillingDtos.CallbackRequest req) {
        billingService.paymentCallback(channel, req);
        return ApiResponse.ok();
    }

    @GetMapping("/subscriptions/current")
    public ApiResponse<Map<String, Object>> current() {
        return ApiResponse.ok(billingService.currentSubscription());
    }
}
