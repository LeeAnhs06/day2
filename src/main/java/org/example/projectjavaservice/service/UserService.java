package org.example.projectjavaservice.service;

import org.example.projectjavaservice.dto.ChangePasswordRequest;
import org.example.projectjavaservice.dto.RegisterRequest;
import org.example.projectjavaservice.dto.UserResponse;
import org.example.projectjavaservice.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;

public interface UserService {
    void register(RegisterRequest request);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(int page, int size);
    Page<UserResponse> searchUserByName(String fullName, int page, int size);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    void changePassword(Long userId, ChangePasswordRequest request);
}