package com.example.privacy.dto;

import jakarta.validation.constraints.NotNull;

public class AnalyzeRequestDTO {
    @NotNull(message = "文档ID不能为空")
    private Long documentId;
    
    public Long getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
}
