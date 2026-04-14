package com.example.privacy.controller;

import com.example.privacy.dto.DocumentUploadDTO;
import com.example.privacy.service.DocumentService;
import com.example.privacy.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/document")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        // 防止路径遍历攻击：仅保留文件名部分
        String safeFilename = sanitizeFilename(originalFilename);
        String fileType = getFileType(safeFilename);
        if ("unknown".equals(fileType)) {
            return Result.error(400, "不支持的文件类型，仅支持 txt/pdf/doc/docx");
        }
        // 第一阶段：假文件上传
        Map<String, Object> data = new HashMap<>();
        data.put("id", System.currentTimeMillis());
        data.put("title", safeFilename);
        data.put("fileName", safeFilename);
        data.put("fileType", fileType);
        data.put("rawText", "假文本内容...");
        return Result.success(data);
    }
    
    @PostMapping("/text")
    public Result<Map<String, Object>> submitText(@Valid @RequestBody DocumentUploadDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", System.currentTimeMillis());
        data.put("title", dto.getTitle());
        data.put("rawText", dto.getText());
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDocument(@PathVariable Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("title", "某 APP 隐私政策");
        data.put("rawText", "我们可能收集您的个人信息用于相关服务...");
        return Result.success(data);
    }
    
    @GetMapping("/{id}/clauses")
    public Result<Map<String, Object>> getClauses(@PathVariable Long id) {
        Map<String, Object> data = new HashMap<>();
        data.put("documentId", id);
        data.put("clauses", new String[]{
            "我们可能收集您的个人信息用于相关服务。",
            "我们可能会与第三方共享您的信息。",
            "您有权查询、更正您的个人信息。"
        });
        return Result.success(data);
    }
    
    @GetMapping("/list")
    public Result<Map<String, Object>> getDocumentList() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", 3);
        data.put("list", new String[]{"文档 1", "文档 2", "文档 3"});
        return Result.success(data);
    }
    
    private String getFileType(String filename) {
        if (filename == null) return "unknown";
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".pdf")) return "PDF";
        if (lowerName.endsWith(".doc") || lowerName.endsWith(".docx")) return "WORD";
        if (lowerName.endsWith(".txt")) return "TXT";
        return "unknown";
    }

    /**
     * 清理文件名，移除路径分隔符防止路径遍历攻击
     */
    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "unnamed";
        }
        // 移除路径分隔符，仅保留文件名
        String sanitized = filename.replace("\\", "/");
        int lastSlash = sanitized.lastIndexOf('/');
        if (lastSlash >= 0) {
            sanitized = sanitized.substring(lastSlash + 1);
        }
        // 移除特殊字符
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5._\\-]", "_");
        return sanitized.isBlank() ? "unnamed" : sanitized;
    }
}
