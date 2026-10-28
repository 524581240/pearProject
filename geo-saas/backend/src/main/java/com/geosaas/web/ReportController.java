package com.geosaas.web;

import com.geosaas.common.ApiResponse;
import com.geosaas.domain.engine.EngineProvider;
import com.geosaas.domain.engine.EngineProviderRepository;
import com.geosaas.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final EngineProviderRepository engineProviderRepository;

    @GetMapping("/reports/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.ok(reportService.overview());
    }

    @GetMapping("/reports/trends")
    public ApiResponse<Map<String, Object>> trends() {
        return ApiResponse.ok(reportService.trends());
    }

    @GetMapping("/engines")
    public ApiResponse<List<EngineProvider>> engines() {
        return ApiResponse.ok(engineProviderRepository.findByEnabledTrueOrderByIdAsc());
    }
}
