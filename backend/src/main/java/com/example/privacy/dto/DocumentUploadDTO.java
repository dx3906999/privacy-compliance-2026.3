package com.example.privacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DocumentUploadDTO {
    @NotBlank(message = "文档标题不能为空")
    @Size(max = 255, message = "文档标题长度不能超过255个字符")
    private String title;

    @NotBlank(message = "文本内容不能为空")
    @Size(max = 100000, message = "文本内容不能超过100000个字符")
    private String text;
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
