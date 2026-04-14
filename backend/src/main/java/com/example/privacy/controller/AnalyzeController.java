package com.example.privacy.controller;

import com.example.privacy.dto.AnalyzeRequestDTO;
import com.example.privacy.service.AnalyzeService;
import com.example.privacy.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/analyze")
public class AnalyzeController {
    
    @Autowired
    private AnalyzeService analyzeService;
    
    @PostMapping("/start")
    public Result<Map<String, Object>> startAnalyze(@Valid @RequestBody AnalyzeRequestDTO dto) {
        // 第一阶段：假分析任务
        Map<String, Object> data = new HashMap<>();
        data.put("taskId", System.currentTimeMillis());
        data.put("documentId", dto.getDocumentId());
        data.put("status", "COMPLETED");
        return Result.success(data);
    }
    
    @GetMapping("/{taskId}")
    public Result<Map<String, Object>> getAnalyzeResult(@PathVariable Long taskId) {
        // 返回假结果
        Map<String, Object> data = analyzeService.getMockResult(taskId);
        return Result.success(data);
    }
    
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getHistory() {
        List<Map<String, Object>> list = new ArrayList<>();
        
        Map<String, Object> record1 = new HashMap<>();
        record1.put("id", 1);
        record1.put("documentTitle", "某 APP 隐私政策");
        record1.put("analyzeTime", "2026-03-13 13:40:00");
        record1.put("riskLevel", "HIGH");
        record1.put("riskScore", 78.5);
        record1.put("taskId", 1);
        list.add(record1);
        
        Map<String, Object> record2 = new HashMap<>();
        record2.put("id", 2);
        record2.put("documentTitle", "电商平台隐私声明");
        record2.put("analyzeTime", "2026-03-12 10:20:00");
        record2.put("riskLevel", "MEDIUM");
        record2.put("riskScore", 45.0);
        record2.put("taskId", 2);
        list.add(record2);
        
        return Result.success(list);
    }
    
    @DeleteMapping("/{taskId}")
    public Result<Void> deleteAnalyze(@PathVariable Long taskId) {
        return Result.success();
    }
}
