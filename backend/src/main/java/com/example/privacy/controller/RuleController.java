package com.example.privacy.controller;

import com.example.privacy.util.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    
    // 使用线程安全的集合存储规则数据
    private final List<Map<String, Object>> ruleList = new CopyOnWriteArrayList<>();
    
    public RuleController() {
        // 初始化假数据
        Map<String, Object> rule1 = new HashMap<>();
        rule1.put("id", 1);
        rule1.put("ruleName", "收集目的描述模糊");
        rule1.put("ruleType", "KEYWORD");
        rule1.put("riskType", "信息收集目的不明确");
        rule1.put("riskLevel", "MEDIUM");
        rule1.put("pattern", "可能，用于相关服务");
        rule1.put("lawReference", "个人信息保护法 第 17 条");
        rule1.put("suggestion", "建议明确说明收集目的、范围和使用场景。");
        rule1.put("enabled", true);
        ruleList.add(rule1);
        
        Map<String, Object> rule2 = new HashMap<>();
        rule2.put("id", 2);
        rule2.put("ruleName", "共享规则缺失必要信息");
        rule2.put("ruleType", "KEYWORD");
        rule2.put("riskType", "共享规则不明确");
        rule2.put("riskLevel", "HIGH");
        rule2.put("pattern", "与第三方共享");
        rule2.put("lawReference", "个人信息保护法 第 23 条");
        rule2.put("suggestion", "建议明确说明共享的第三方类型、共享目的和信息类型。");
        rule2.put("enabled", true);
        ruleList.add(rule2);
        
        Map<String, Object> rule3 = new HashMap<>();
        rule3.put("id", 3);
        rule3.put("ruleName", "用户权利描述不完整");
        rule3.put("ruleType", "MISSING_CHECK");
        rule3.put("riskType", "用户权利说明不足");
        rule3.put("riskLevel", "MEDIUM");
        rule3.put("pattern", "查询，更正，删除，撤回");
        rule3.put("lawReference", "个人信息保护法 第 44 条");
        rule3.put("suggestion", "建议补充说明删除、撤回同意等权利及行使方式。");
        rule3.put("enabled", true);
        ruleList.add(rule3);
    }
    
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getRuleList() {
        return Result.success(ruleList);
    }
    
    @PostMapping("/add")
    public Result<Map<String, Object>> addRule(@RequestBody Map<String, Object> rule) {
        rule.put("id", System.currentTimeMillis());
        ruleList.add(rule);
        return Result.success(rule);
    }
    
    @PutMapping("/update")
    public Result<Void> updateRule(@RequestBody Map<String, Object> rule) {
        Object idObj = rule.get("id");
        if (idObj == null) {
            return Result.error(400, "规则ID不能为空");
        }
        try {
            Long id = Long.valueOf(idObj.toString());
            for (int i = 0; i < ruleList.size(); i++) {
                Object existingId = ruleList.get(i).get("id");
                if (existingId != null && Long.valueOf(existingId.toString()).equals(id)) {
                    ruleList.set(i, rule);
                    return Result.success();
                }
            }
            return Result.error(404, "未找到指定规则");
        } catch (NumberFormatException e) {
            return Result.error(400, "规则ID格式错误");
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        boolean removed = ruleList.removeIf(rule -> {
            Object ruleId = rule.get("id");
            if (ruleId == null) return false;
            try {
                return Long.valueOf(ruleId.toString()).equals(id);
            } catch (NumberFormatException e) {
                return false;
            }
        });
        if (!removed) {
            return Result.error(404, "未找到指定规则");
        }
        return Result.success();
    }
}
