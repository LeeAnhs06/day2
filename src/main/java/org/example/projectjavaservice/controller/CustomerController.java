package org.example.projectjavaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.ApiResponse;
import org.example.projectjavaservice.dto.ChangePasswordRequest;
import org.example.projectjavaservice.security.CustomUserDetails;
import org.example.projectjavaservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/profile") // Khai báo chuẩn khu vực Customer
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        // Lấy userId từ Token
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        userService.changePassword(userId, request);
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Password changed successfully")
                .data(null)
                .build());
    }
}