package com.geosaas.config;

import com.geosaas.domain.billing.Plan;
import com.geosaas.domain.billing.PlanRepository;
import com.geosaas.domain.billing.Subscription;
import com.geosaas.domain.billing.SubscriptionRepository;
import com.geosaas.domain.engine.EngineProvider;
import com.geosaas.domain.engine.EngineProviderRepository;
import com.geosaas.domain.tenant.Tenant;
import com.geosaas.domain.tenant.TenantRepository;
import com.geosaas.domain.user.UserAccount;
import com.geosaas.domain.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PlanRepository planRepository;
    private final EngineProviderRepository engineProviderRepository;
    private final TenantRepository tenantRepository;
    private final UserAccountRepository userAccountRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedPlans();
        seedEngines();
        seedAdminAndDemo();
    }

    private void seedPlans() {
        if (planRepository.count() > 0) return;
        planRepository.save(plan("trial", "试用版", "14天试用", 0, 0, 1, 10, 3, 200, 2, 14, 1));
        planRepository.save(plan("starter", "入门版", "中小团队起步监测", 99, 990, 1, 30, 4, 1500, 3, 60, 2));
        planRepository.save(plan("pro", "专业版", "多引擎日常监测", 299, 2990, 3, 100, 6, 8000, 8, 180, 3));
        planRepository.save(plan("enterprise", "企业版", "全引擎 + 高额度", 999, 9990, 10, 500, 8, 50000, 30, 365, 4));
    }

    private Plan plan(String code, String name, String desc, int month, int year,
                      int brands, int prompts, int engines, int responses, int seats, int days, int sort) {
        Plan p = new Plan();
        p.setCode(code);
        p.setName(name);
        p.setDescription(desc);
        p.setPriceMonthly(BigDecimal.valueOf(month));
        p.setPriceYearly(BigDecimal.valueOf(year));
        p.setBrandLimit(brands);
        p.setPromptLimit(prompts);
        p.setEngineLimit(engines);
        p.setMonthlyResponseLimit(responses);
        p.setSeatLimit(seats);
        p.setHistoryDays(days);
        p.setEnabled(true);
        p.setSortOrder(sort);
        return p;
    }

    private void seedEngines() {
        if (engineProviderRepository.count() > 0) return;
        List.of(
                eng("chatgpt", "ChatGPT", "GLOBAL"),
                eng("perplexity", "Perplexity", "GLOBAL"),
                eng("gemini", "Gemini", "GLOBAL"),
                eng("deepseek", "DeepSeek", "CN"),
                eng("doubao", "豆包", "CN"),
                eng("kimi", "Kimi", "CN"),
                eng("qwen", "通义千问", "CN"),
                eng("yuanbao", "腾讯元宝", "CN")
        ).forEach(engineProviderRepository::save);
    }

    private EngineProvider eng(String code, String name, String region) {
        EngineProvider e = new EngineProvider();
        e.setCode(code);
        e.setName(name);
        e.setRegion(region);
        e.setEnabled(true);
        e.setAdapterType("MOCK");
        return e;
    }

    private void seedAdminAndDemo() {
        if (userAccountRepository.findByUsername("admin").isPresent()) return;

        Tenant platform = new Tenant();
        platform.setCode("platform");
        platform.setName("GEO平台运营");
        platform.setType("PLATFORM");
        platform.setStatus("ACTIVE");
        tenantRepository.save(platform);

        UserAccount admin = new UserAccount();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setDisplayName("平台管理员");
        admin.setEmail("admin@geosaas.local");
        admin.setTenantId(platform.getId());
        admin.setRole("PLATFORM_ADMIN");
        userAccountRepository.save(admin);

        Tenant demo = new Tenant();
        demo.setCode("demo");
        demo.setName("演示品牌团队");
        demo.setType("CUSTOMER");
        demo.setStatus("ACTIVE");
        tenantRepository.save(demo);

        UserAccount demoUser = new UserAccount();
        demoUser.setUsername("demo");
        demoUser.setPasswordHash(passwordEncoder.encode("demo123"));
        demoUser.setDisplayName("演示用户");
        demoUser.setEmail("demo@geosaas.local");
        demoUser.setTenantId(demo.getId());
        demoUser.setRole("OWNER");
        userAccountRepository.save(demoUser);

        Plan starter = planRepository.findByCode("starter").orElseThrow();
        Subscription sub = new Subscription();
        sub.setTenantId(demo.getId());
        sub.setPlanId(starter.getId());
        sub.setPlanCode(starter.getCode());
        sub.setStatus("ACTIVE");
        sub.setBillingCycle("MONTHLY");
        sub.setStartAt(LocalDateTime.now().minusDays(1));
        sub.setEndAt(LocalDateTime.now().plusMonths(1));
        sub.setBrandLimit(starter.getBrandLimit());
        sub.setPromptLimit(starter.getPromptLimit());
        sub.setEngineLimit(starter.getEngineLimit());
        sub.setMonthlyResponseLimit(starter.getMonthlyResponseLimit());
        sub.setSeatLimit(starter.getSeatLimit());
        sub.setHistoryDays(starter.getHistoryDays());
        subscriptionRepository.save(sub);
    }
}
