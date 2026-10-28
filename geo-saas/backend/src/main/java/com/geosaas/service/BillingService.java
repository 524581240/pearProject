package com.geosaas.service;

import com.geosaas.common.BizException;
import com.geosaas.domain.billing.OrderInfo;
import com.geosaas.domain.billing.OrderInfoRepository;
import com.geosaas.domain.billing.PaymentRecord;
import com.geosaas.domain.billing.PaymentRecordRepository;
import com.geosaas.domain.billing.Plan;
import com.geosaas.domain.billing.PlanRepository;
import com.geosaas.domain.billing.Subscription;
import com.geosaas.domain.billing.SubscriptionRepository;
import com.geosaas.domain.billing.UsageCounter;
import com.geosaas.security.SecurityUtils;
import com.geosaas.web.dto.BillingDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final PlanRepository planRepository;
    private final OrderInfoRepository orderInfoRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final QuotaService quotaService;

    @Value("${geosaas.payment.mode:mock}")
    private String paymentMode;

    public List<Plan> listPlans() {
        return planRepository.findByEnabledTrueOrderBySortOrderAsc();
    }

    public List<Plan> adminListPlans() {
        return planRepository.findAll();
    }

    @Transactional
    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }

    @Transactional
    public OrderInfo createOrder(BillingDtos.CreateOrderRequest req) {
        Long tenantId = SecurityUtils.tenantId();
        Plan plan = planRepository.findByCode(req.getPlanCode())
                .orElseThrow(() -> new BizException("套餐不存在"));
        if (!Boolean.TRUE.equals(plan.getEnabled())) {
            throw new BizException("套餐已下架");
        }
        String cycle = "YEARLY".equalsIgnoreCase(req.getBillingCycle()) ? "YEARLY" : "MONTHLY";
        BigDecimal amount = "YEARLY".equals(cycle) ? plan.getPriceYearly() : plan.getPriceMonthly();

        OrderInfo order = new OrderInfo();
        order.setOrderNo("GEO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setTenantId(tenantId);
        order.setPlanId(plan.getId());
        order.setPlanCode(plan.getCode());
        order.setBillingCycle(cycle);
        order.setAmount(amount);
        order.setStatus("PENDING");
        return orderInfoRepository.save(order);
    }

    public List<OrderInfo> myOrders() {
        return orderInfoRepository.findByTenantIdOrderByIdDesc(SecurityUtils.tenantId());
    }

    @Transactional
    public Map<String, Object> pay(Long orderId, BillingDtos.PayRequest req) {
        OrderInfo order = orderInfoRepository.findByIdAndTenantId(orderId, SecurityUtils.tenantId())
                .orElseThrow(() -> new BizException("订单不存在"));
        if (!"PENDING".equals(order.getStatus())) {
            throw new BizException("订单状态不可支付");
        }
        String channel = req.getChannel() == null ? paymentMode.toUpperCase() : req.getChannel().toUpperCase();
        order.setPayChannel(channel);

        // Mock instant success; real channels would return redirect / QR
        if ("MOCK".equals(channel) || "mock".equalsIgnoreCase(paymentMode)) {
            markPaid(order, "MOCK-" + UUID.randomUUID());
            Map<String, Object> result = new HashMap<>();
            result.put("mode", "MOCK");
            result.put("status", "PAID");
            result.put("orderNo", order.getOrderNo());
            return result;
        }

        // Placeholder for wechat/alipay: return mock QR payload
        Map<String, Object> result = new HashMap<>();
        result.put("mode", channel);
        result.put("status", "PENDING");
        result.put("orderNo", order.getOrderNo());
        result.put("payUrl", "https://pay.example.com/mock/" + order.getOrderNo());
        return result;
    }

    @Transactional
    public void paymentCallback(String channel, BillingDtos.CallbackRequest req) {
        OrderInfo order = orderInfoRepository.findByOrderNo(req.getOrderNo())
                .orElseThrow(() -> new BizException("订单不存在"));
        if ("PAID".equals(order.getStatus())) {
            return;
        }
        order.setPayChannel(channel.toUpperCase());
        markPaid(order, req.getChannelTradeNo() == null ? channel + "-" + UUID.randomUUID() : req.getChannelTradeNo());
    }

    private void markPaid(OrderInfo order, String tradeNo) {
        order.setStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        orderInfoRepository.save(order);

        PaymentRecord payment = new PaymentRecord();
        payment.setTenantId(order.getTenantId());
        payment.setOrderId(order.getId());
        payment.setOrderNo(order.getOrderNo());
        payment.setChannel(order.getPayChannel());
        payment.setAmount(order.getAmount());
        payment.setStatus("SUCCESS");
        payment.setChannelTradeNo(tradeNo);
        payment.setPaidAt(LocalDateTime.now());
        paymentRecordRepository.save(payment);

        Plan plan = planRepository.findById(order.getPlanId())
                .orElseThrow(() -> new BizException("套餐不存在"));
        Subscription sub = subscriptionRepository.findByTenantId(order.getTenantId())
                .orElseGet(Subscription::new);
        sub.setTenantId(order.getTenantId());
        sub.setPlanId(plan.getId());
        sub.setPlanCode(plan.getCode());
        sub.setStatus("ACTIVE");
        sub.setBillingCycle(order.getBillingCycle());
        LocalDateTime start = LocalDateTime.now();
        sub.setStartAt(start);
        sub.setEndAt("YEARLY".equals(order.getBillingCycle()) ? start.plusYears(1) : start.plusMonths(1));
        sub.setBrandLimit(plan.getBrandLimit());
        sub.setPromptLimit(plan.getPromptLimit());
        sub.setEngineLimit(plan.getEngineLimit());
        sub.setMonthlyResponseLimit(plan.getMonthlyResponseLimit());
        sub.setSeatLimit(plan.getSeatLimit());
        sub.setHistoryDays(plan.getHistoryDays());
        subscriptionRepository.save(sub);
    }

    public Map<String, Object> currentSubscription() {
        Long tenantId = SecurityUtils.tenantId();
        Subscription sub = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new BizException("暂无订阅"));
        UsageCounter usage = quotaService.currentUsage(tenantId);
        Map<String, Object> map = new HashMap<>();
        map.put("subscription", sub);
        map.put("usage", usage);
        return map;
    }

    public List<OrderInfo> adminOrders() {
        return orderInfoRepository.findAll();
    }

    public List<Subscription> adminSubscriptions() {
        return subscriptionRepository.findAll();
    }
}
