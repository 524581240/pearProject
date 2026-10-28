package com.geosaas.web;

import com.geosaas.common.ApiResponse;
import com.geosaas.domain.brand.Brand;
import com.geosaas.domain.brand.Competitor;
import com.geosaas.service.BrandService;
import com.geosaas.web.dto.BrandDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ApiResponse<List<Brand>> list() {
        return ApiResponse.ok(brandService.list());
    }

    @PostMapping
    public ApiResponse<Brand> create(@Valid @RequestBody BrandDtos.UpsertRequest req) {
        return ApiResponse.ok(brandService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Brand> update(@PathVariable Long id, @Valid @RequestBody BrandDtos.UpsertRequest req) {
        return ApiResponse.ok(brandService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/competitors")
    public ApiResponse<List<Competitor>> competitors(@PathVariable Long id) {
        return ApiResponse.ok(brandService.listCompetitors(id));
    }

    @PostMapping("/{id}/competitors")
    public ApiResponse<Competitor> addCompetitor(@PathVariable Long id,
                                                 @Valid @RequestBody BrandDtos.CompetitorRequest req) {
        return ApiResponse.ok(brandService.addCompetitor(id, req));
    }

    @DeleteMapping("/competitors/{competitorId}")
    public ApiResponse<Void> removeCompetitor(@PathVariable Long competitorId) {
        brandService.removeCompetitor(competitorId);
        return ApiResponse.ok();
    }
}
