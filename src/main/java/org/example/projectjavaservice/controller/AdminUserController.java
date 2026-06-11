package org.example.projectjavaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.ApiResponse;
import org.example.projectjavaservice.dto.UserResponse;
import org.example.projectjavaservice.dto.UserUpdateRequest;
import org.example.projectjavaservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users") // Khai báo chuẩn khu vực Admin
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String fullName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> users = userService.searchUserByName(fullName, page, size);
        return ResponseEntity.ok(ApiResponse.<Page<UserResponse>>builder()
                .success(true)
                .message("Search results retrieved successfully")
                .data(users)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User updated successfully")
                .data(user)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // Sửa chuẩn 204 No Content như SRS
    }
}