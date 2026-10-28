package com.geosaas.service;

import com.geosaas.common.BizException;
import com.geosaas.domain.billing.Subscription;
import com.geosaas.domain.billing.SubscriptionRepository;
import com.geosaas.domain.billing.UsageCounter;
import com.geosaas.domain.billing.UsageCounterRepository;
import com.geosaas.domain.brand.BrandRepository;
import com.geosaas.domain.prompt.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class QuotaService {
    private final SubscriptionRepository subscriptionRepository;
    private final UsageCounterRepository usageCounterRepository;
    private final BrandRepository brandRepository;
    private final PromptRepository promptRepository;

    public Subscription requireActive(Long tenantId) {
        Subscription sub = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new BizException("未开通订阅，请先购买套餐"));
        if ("EXPIRED".equals(sub.getStatus()) || "CANCELLED".equals(sub.getStatus())) {
            throw new BizException("订阅已失效，请续费");
        }
        if (sub.getEndAt() != null && sub.getEndAt().isBefore(java.time.LocalDateTime.now())) {
            sub.setStatus("EXPIRED");
            subscriptionRepository.save(sub);
            throw new BizException("订阅已过期，请续费");
        }
        return sub;
    }

    public void checkBrandQuota(Long tenantId) {
        Subscription sub = requireActive(tenantId);
        long count = brandRepository.countByTenantIdAndStatus(tenantId, "ACTIVE");
        if (count >= sub.getBrandLimit()) {
            throw new BizException("品牌数已达套餐上限 " + sub.getBrandLimit());
        }
    }

    public void checkPromptQuota(Long tenantId) {
        Subscription sub = requireActive(tenantId);
        long count = promptRepository.countByTenantIdAndStatus(tenantId, "ACTIVE");
        if (count >= sub.getPromptLimit()) {
            throw new BizException("Prompt 数已达套餐上限 " + sub.getPromptLimit());
        }
    }

    public void checkEngineQuota(Long tenantId, int engineCount) {
        Subscription sub = requireActive(tenantId);
        if (engineCount > sub.getEngineLimit()) {
            throw new BizException("引擎数超过套餐上限 " + sub.getEngineLimit());
        }
    }

    @Transactional
    public void consumeResponses(Long tenantId, int count) {
        Subscription sub = requireActive(tenantId);
        String ym = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        UsageCounter counter = usageCounterRepository.findByTenantIdAndYearMonth(tenantId, ym)
                .orElseGet(() -> {
                    UsageCounter c = new UsageCounter();
                    c.setTenantId(tenantId);
                    c.setYearMonth(ym);
                    c.setResponseUsed(0);
                    return c;
                });
        if (counter.getResponseUsed() + count > sub.getMonthlyResponseLimit()) {
            throw new BizException("本月响应额度不足（限额 " + sub.getMonthlyResponseLimit() + "）");
        }
        counter.setResponseUsed(counter.getResponseUsed() + count);
        usageCounterRepository.save(counter);
    }

    public UsageCounter currentUsage(Long tenantId) {
        String ym = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return usageCounterRepository.findByTenantIdAndYearMonth(tenantId, ym)
                .orElseGet(() -> {
                    UsageCounter c = new UsageCounter();
                    c.setTenantId(tenantId);
                    c.setYearMonth(ym);
                    c.setResponseUsed(0);
                    return c;
                });
    }
}
