package com.geosaas.web;

import com.geosaas.common.ApiResponse;
import com.geosaas.domain.job.MonitorJob;
import com.geosaas.domain.job.RunBatch;
import com.geosaas.service.MonitorJobService;
import com.geosaas.web.dto.JobDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobController {
    private final MonitorJobService monitorJobService;

    @GetMapping("/jobs")
    public ApiResponse<List<MonitorJob>> listJobs() {
        return ApiResponse.ok(monitorJobService.list());
    }

    @PostMapping("/jobs")
    public ApiResponse<MonitorJob> create(@Valid @RequestBody JobDtos.UpsertRequest req) {
        return ApiResponse.ok(monitorJobService.create(req));
    }

    @PostMapping("/jobs/{id}/run")
    public ApiResponse<RunBatch> run(@PathVariable Long id) {
        return ApiResponse.ok(monitorJobService.runNow(id));
    }

    @PostMapping("/verify")
    public ApiResponse<RunBatch> verify(@Valid @RequestBody JobDtos.VerifyRequest req) {
        return ApiResponse.ok(monitorJobService.verifyOnce(req));
    }

    @GetMapping("/batches")
    public ApiResponse<List<RunBatch>> batches() {
        return ApiResponse.ok(monitorJobService.listBatches());
    }

    @GetMapping("/batches/{id}")
    public ApiResponse<JobDtos.BatchDetail> batchDetail(@PathVariable Long id) {
        return ApiResponse.ok(monitorJobService.batchDetail(id));
    }
}
