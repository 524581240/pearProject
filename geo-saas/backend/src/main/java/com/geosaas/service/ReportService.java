package com.geosaas.service;

import com.geosaas.domain.job.RunItem;
import com.geosaas.domain.job.RunItemRepository;
import com.geosaas.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final RunItemRepository runItemRepository;

    public Map<String, Object> overview() {
        Long tenantId = SecurityUtils.tenantId();
        List<RunItem> items = runItemRepository.findByTenantIdAndStatusOrderByIdDesc(tenantId, "SUCCESS");
        long total = items.size();
        long mentioned = items.stream().filter(i -> Boolean.TRUE.equals(i.getMentioned())).count();
        double mentionRate = total == 0 ? 0 : (mentioned * 100.0 / total);
        double avgSentiment = items.stream()
                .filter(i -> i.getSentimentScore() != null)
                .mapToDouble(RunItem::getSentimentScore)
                .average().orElse(0);
        double avgRank = items.stream()
                .filter(i -> i.getPositionRank() != null)
                .mapToInt(RunItem::getPositionRank)
                .average().orElse(0);

        Map<String, List<RunItem>> byEngine = items.stream()
                .collect(Collectors.groupingBy(RunItem::getEngineCode, LinkedHashMap::new, Collectors.toList()));
        List<Map<String, Object>> engineStats = new ArrayList<>();
        byEngine.forEach((code, list) -> {
            long m = list.stream().filter(i -> Boolean.TRUE.equals(i.getMentioned())).count();
            Map<String, Object> row = new HashMap<>();
            row.put("engineCode", code);
            row.put("total", list.size());
            row.put("mentioned", m);
            row.put("mentionRate", list.isEmpty() ? 0 : Math.round(m * 1000.0 / list.size()) / 10.0);
            engineStats.add(row);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("totalRuns", total);
        result.put("mentionedRuns", mentioned);
        result.put("mentionRate", Math.round(mentionRate * 10.0) / 10.0);
        result.put("avgSentiment", Math.round(avgSentiment * 100.0) / 100.0);
        result.put("avgRank", Math.round(avgRank * 10.0) / 10.0);
        result.put("engines", engineStats);
        result.put("recent", items.stream().limit(20).toList());
        return result;
    }

    public Map<String, Object> trends() {
        List<RunItem> items = runItemRepository.findByTenantIdAndStatusOrderByIdDesc(SecurityUtils.tenantId(), "SUCCESS");
        Map<String, long[]> dayMap = new LinkedHashMap<>();
        for (int i = items.size() - 1; i >= 0; i--) {
            RunItem item = items.get(i);
            String day = item.getCreatedAt().toLocalDate().toString();
            dayMap.computeIfAbsent(day, k -> new long[]{0, 0});
            dayMap.get(day)[0]++;
            if (Boolean.TRUE.equals(item.getMentioned())) {
                dayMap.get(day)[1]++;
            }
        }
        List<String> days = new ArrayList<>(dayMap.keySet());
        List<Double> rates = new ArrayList<>();
        List<Long> totals = new ArrayList<>();
        for (String d : days) {
            long[] arr = dayMap.get(d);
            totals.add(arr[0]);
            rates.add(arr[0] == 0 ? 0 : Math.round(arr[1] * 1000.0 / arr[0]) / 10.0);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("days", days);
        result.put("totals", totals);
        result.put("mentionRates", rates);
        return result;
    }
}
