package org.example.projectjavaservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.*;
import org.example.projectjavaservice.service.AuthService;
import org.example.projectjavaservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .success(true)
                        .message("Registered successfully")
                        .data(null)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successfully")
                .data(loginResponse)
                .build());
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
        // Lấy header Authorization
        String authHeader = request.getHeader("Authorization");

        // Gọi service xử lý blacklist
        authService.logout(authHeader);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Logged out successfully")
                .data(null)
                .build());
    }

    // Thêm hàm này vào AuthController

@PostMapping("/refresh")
public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    LoginResponse loginResponse = authService.refreshToken(request);
    return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
            .success(true)
            .message("Token refreshed successfully")
            .data(loginResponse)
            .build());
}
}