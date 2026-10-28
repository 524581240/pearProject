package com.geosaas.web;

import com.geosaas.common.ApiResponse;
import com.geosaas.domain.billing.OrderInfo;
import com.geosaas.domain.billing.Plan;
import com.geosaas.domain.billing.Subscription;
import com.geosaas.domain.engine.EngineProvider;
import com.geosaas.domain.engine.EngineProviderRepository;
import com.geosaas.domain.job.MonitorJob;
import com.geosaas.domain.job.MonitorJobRepository;
import com.geosaas.domain.tenant.Tenant;
import com.geosaas.domain.tenant.TenantRepository;
import com.geosaas.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final TenantRepository tenantRepository;
    private final BillingService billingService;
    private final EngineProviderRepository engineProviderRepository;
    private final MonitorJobRepository monitorJobRepository;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> map = new HashMap<>();
        map.put("tenantCount", tenantRepository.count());
        map.put("orderCount", billingService.adminOrders().size());
        map.put("planCount", billingService.adminListPlans().size());
        map.put("engineCount", engineProviderRepository.count());
        map.put("jobCount", monitorJobRepository.count());
        return ApiResponse.ok(map);
    }

    @GetMapping("/tenants")
    public ApiResponse<List<Tenant>> tenants() {
        return ApiResponse.ok(tenantRepository.findAll());
    }

    @GetMapping("/plans")
    public ApiResponse<List<Plan>> plans() {
        return ApiResponse.ok(billingService.adminListPlans());
    }

    @PostMapping("/plans")
    public ApiResponse<Plan> savePlan(@RequestBody Plan plan) {
        return ApiResponse.ok(billingService.savePlan(plan));
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderInfo>> orders() {
        return ApiResponse.ok(billingService.adminOrders());
    }

    @GetMapping("/subscriptions")
    public ApiResponse<List<Subscription>> subscriptions() {
        return ApiResponse.ok(billingService.adminSubscriptions());
    }

    @GetMapping("/engines")
    public ApiResponse<List<EngineProvider>> engines() {
        return ApiResponse.ok(engineProviderRepository.findAll());
    }

    @PostMapping("/engines")
    public ApiResponse<EngineProvider> saveEngine(@RequestBody EngineProvider provider) {
        return ApiResponse.ok(engineProviderRepository.save(provider));
    }

    @GetMapping("/jobs")
    public ApiResponse<List<MonitorJob>> jobs() {
        return ApiResponse.ok(monitorJobRepository.findAll());
    }
}
