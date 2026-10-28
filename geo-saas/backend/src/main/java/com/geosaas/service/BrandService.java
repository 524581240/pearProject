package com.geosaas.service;

import com.geosaas.common.BizException;
import com.geosaas.domain.brand.Brand;
import com.geosaas.domain.brand.BrandRepository;
import com.geosaas.domain.brand.Competitor;
import com.geosaas.domain.brand.CompetitorRepository;
import com.geosaas.security.SecurityUtils;
import com.geosaas.web.dto.BrandDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final CompetitorRepository competitorRepository;
    private final QuotaService quotaService;

    public List<Brand> list() {
        return brandRepository.findByTenantIdOrderByIdDesc(SecurityUtils.tenantId());
    }

    @Transactional
    public Brand create(BrandDtos.UpsertRequest req) {
        Long tenantId = SecurityUtils.tenantId();
        quotaService.checkBrandQuota(tenantId);
        Brand brand = new Brand();
        brand.setTenantId(tenantId);
        apply(brand, req);
        return brandRepository.save(brand);
    }

    @Transactional
    public Brand update(Long id, BrandDtos.UpsertRequest req) {
        Brand brand = getOwned(id);
        apply(brand, req);
        return brandRepository.save(brand);
    }

    @Transactional
    public void delete(Long id) {
        Brand brand = getOwned(id);
        brand.setStatus("ARCHIVED");
        brandRepository.save(brand);
    }

    public List<Competitor> listCompetitors(Long brandId) {
        getOwned(brandId);
        return competitorRepository.findByBrandIdAndTenantId(brandId, SecurityUtils.tenantId());
    }

    @Transactional
    public Competitor addCompetitor(Long brandId, BrandDtos.CompetitorRequest req) {
        Brand brand = getOwned(brandId);
        Competitor c = new Competitor();
        c.setTenantId(brand.getTenantId());
        c.setBrandId(brandId);
        c.setName(req.getName());
        c.setAliases(req.getAliases());
        c.setDomain(req.getDomain());
        return competitorRepository.save(c);
    }

    @Transactional
    public void removeCompetitor(Long competitorId) {
        Competitor c = competitorRepository.findById(competitorId)
                .orElseThrow(() -> new BizException("竞品不存在"));
        if (!c.getTenantId().equals(SecurityUtils.tenantId())) {
            throw new BizException(403, "无权限");
        }
        competitorRepository.delete(c);
    }

    public Brand getOwned(Long id) {
        return brandRepository.findByIdAndTenantId(id, SecurityUtils.tenantId())
                .orElseThrow(() -> new BizException("品牌不存在"));
    }

    private void apply(Brand brand, BrandDtos.UpsertRequest req) {
        brand.setName(req.getName());
        brand.setAliases(req.getAliases());
        brand.setDomain(req.getDomain());
        brand.setIndustry(req.getIndustry());
        if (req.getLocale() != null) brand.setLocale(req.getLocale());
        if (req.getCountry() != null) brand.setCountry(req.getCountry());
        brand.setStatus("ACTIVE");
    }
}
