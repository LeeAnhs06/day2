package org.example.projectjavaservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.ChangePasswordRequest;
import org.example.projectjavaservice.dto.RegisterRequest;
import org.example.projectjavaservice.dto.UserResponse;
import org.example.projectjavaservice.dto.UserUpdateRequest;
import org.example.projectjavaservice.entity.Role;
import org.example.projectjavaservice.entity.User;
import org.example.projectjavaservice.exception.BadRequestException;
import org.example.projectjavaservice.exception.ConflictException;
import org.example.projectjavaservice.exception.NotFoundException;
import org.example.projectjavaservice.repository.UserRepository;
import org.example.projectjavaservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.CUSTOMER)
                .isEnabled(true)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToResponse);
    }

    @Override
    public Page<UserResponse> searchUserByName(String fullName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findByFullNameContainingIgnoreCase(fullName, pageable);
        return users.map(this::mapToResponse);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .build();
    }

    @Override
public void changePassword(Long userId, ChangePasswordRequest request) {
    // 1. Tìm user trong DB
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

    // 2. Kiểm tra mật khẩu hiện tại nhập vào có khớp với DB không
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
        throw new BadRequestException("Current password is incorrect");
    }

    // 3. Mã hóa mật khẩu mới và cập nhật
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
}
}