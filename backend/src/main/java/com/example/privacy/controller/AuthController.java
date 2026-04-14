package com.example.privacy.controller;

import com.example.privacy.dto.LoginDTO;
import com.example.privacy.dto.RegisterDTO;
import com.example.privacy.service.AuthService;
import com.example.privacy.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        // 第一阶段：假登录验证
        if ("admin".equals(loginDTO.getUsername()) && "123456".equals(loginDTO.getPassword())) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", "fake-jwt-token-" + System.currentTimeMillis());
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", 1);
            userInfo.put("username", "admin");
            userInfo.put("role", "ADMIN");
            data.put("userInfo", userInfo);
            
            return Result.success(data);
        }
        return Result.error(401, "用户名或密码错误");
    }
    
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        // 第一阶段：假注册
        Map<String, Object> data = new HashMap<>();
        data.put("id", System.currentTimeMillis());
        data.put("username", registerDTO.getUsername());
        return Result.success(data);
    }
    
    @GetMapping("/me")
    public Result<Map<String, Object>> getCurrentUser() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", 1);
        userInfo.put("username", "admin");
        userInfo.put("role", "ADMIN");
        return Result.success(userInfo);
    }
}
