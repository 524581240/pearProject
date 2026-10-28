package com.geosaas.service;

import com.geosaas.common.BizException;
import com.geosaas.domain.billing.Plan;
import com.geosaas.domain.billing.PlanRepository;
import com.geosaas.domain.billing.Subscription;
import com.geosaas.domain.billing.SubscriptionRepository;
import com.geosaas.domain.tenant.Tenant;
import com.geosaas.domain.tenant.TenantRepository;
import com.geosaas.domain.user.UserAccount;
import com.geosaas.domain.user.UserAccountRepository;
import com.geosaas.security.JwtService;
import com.geosaas.security.SecurityUtils;
import com.geosaas.security.UserPrincipal;
import com.geosaas.web.dto.AuthDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthDtos.TokenResponse register(AuthDtos.RegisterRequest req) {
        if (userAccountRepository.existsByUsername(req.getUsername())) {
            throw new BizException("用户名已存在");
        }
        Tenant tenant = new Tenant();
        tenant.setCode("t_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        tenant.setName(req.getCompanyName() == null || req.getCompanyName().isBlank()
                ? req.getUsername() + "的团队" : req.getCompanyName());
        tenant.setType("CUSTOMER");
        tenant.setStatus("ACTIVE");
        tenantRepository.save(tenant);

        UserAccount user = new UserAccount();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setDisplayName(req.getDisplayName() == null ? req.getUsername() : req.getDisplayName());
        user.setTenantId(tenant.getId());
        user.setRole("OWNER");
        userAccountRepository.save(user);

        Plan trial = planRepository.findByCode("trial")
                .orElseGet(() -> planRepository.findByEnabledTrueOrderBySortOrderAsc().stream().findFirst()
                        .orElseThrow(() -> new BizException("未配置套餐")));
        Subscription sub = new Subscription();
        sub.setTenantId(tenant.getId());
        sub.setPlanId(trial.getId());
        sub.setPlanCode(trial.getCode());
        sub.setStatus("TRIAL");
        sub.setBillingCycle("MONTHLY");
        sub.setStartAt(LocalDateTime.now());
        sub.setEndAt(LocalDateTime.now().plusDays(14));
        copyQuota(sub, trial);
        subscriptionRepository.save(sub);

        return issue(user);
    }

    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest req) {
        UserAccount user = userAccountRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BizException(401, "用户名或密码错误"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BizException(401, "用户名或密码错误");
        }
        return issue(user);
    }

    public AuthDtos.MeResponse me() {
        UserPrincipal p = SecurityUtils.currentUser();
        UserAccount user = userAccountRepository.findById(p.getUserId())
                .orElseThrow(() -> new BizException("用户不存在"));
        Tenant tenant = tenantRepository.findById(user.getTenantId())
                .orElseThrow(() -> new BizException("租户不存在"));
        AuthDtos.MeResponse resp = new AuthDtos.MeResponse();
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setDisplayName(user.getDisplayName());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole());
        resp.setTenantId(tenant.getId());
        resp.setTenantName(tenant.getName());
        resp.setTenantCode(tenant.getCode());
        return resp;
    }

    private AuthDtos.TokenResponse issue(UserAccount user) {
        String token = jwtService.createToken(user.getId(), user.getTenantId(), user.getUsername(), user.getRole());
        AuthDtos.TokenResponse resp = new AuthDtos.TokenResponse();
        resp.setToken(token);
        resp.setUserId(user.getId());
        resp.setTenantId(user.getTenantId());
        resp.setUsername(user.getUsername());
        resp.setRole(user.getRole());
        return resp;
    }

    private void copyQuota(Subscription sub, Plan plan) {
        sub.setBrandLimit(plan.getBrandLimit());
        sub.setPromptLimit(plan.getPromptLimit());
        sub.setEngineLimit(plan.getEngineLimit());
        sub.setMonthlyResponseLimit(plan.getMonthlyResponseLimit());
        sub.setSeatLimit(plan.getSeatLimit());
        sub.setHistoryDays(plan.getHistoryDays());
    }
}
