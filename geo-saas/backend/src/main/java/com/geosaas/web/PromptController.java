package com.geosaas.web;

import com.geosaas.common.ApiResponse;
import com.geosaas.domain.prompt.Prompt;
import com.geosaas.domain.prompt.PromptVersion;
import com.geosaas.service.PromptService;
import com.geosaas.web.dto.PromptDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prompts")
@RequiredArgsConstructor
public class PromptController {
    private final PromptService promptService;

    @GetMapping
    public ApiResponse<List<Prompt>> list(@RequestParam(required = false) Long brandId) {
        return ApiResponse.ok(promptService.list(brandId));
    }

    @PostMapping
    public ApiResponse<Prompt> create(@Valid @RequestBody PromptDtos.UpsertRequest req) {
        return ApiResponse.ok(promptService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Prompt> update(@PathVariable Long id, @Valid @RequestBody PromptDtos.UpsertRequest req) {
        return ApiResponse.ok(promptService.update(id, req));
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<PromptVersion>> versions(@PathVariable Long id) {
        return ApiResponse.ok(promptService.versions(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> archive(@PathVariable Long id) {
        promptService.archive(id);
        return ApiResponse.ok();
    }
}
