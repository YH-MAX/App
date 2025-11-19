package com.smartwater.backend.controller;

import com.smartwater.backend.model.User;
import com.smartwater.backend.service.UserService;
import com.smartwater.backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }


    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {

        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // 1. 使用 AuthenticationManager 验证账号密码
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 2. 获取用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. 生成 JWT token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // 4. 返回 JSON 格式响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", email);
        response.put("message", "Login successful!");

        return response;
    }
}
