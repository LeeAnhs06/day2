package org.example.projectjavaservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.Request.*;
import org.example.projectjavaservice.dto.Response.UserResponse;
import org.example.projectjavaservice.entity.enums.Role;
import org.example.projectjavaservice.entity.User;
import org.example.projectjavaservice.exception.BadRequestException;
import org.example.projectjavaservice.exception.ConflictException;
import org.example.projectjavaservice.exception.NotFoundException;
import org.example.projectjavaservice.repository.UserRepository;
import org.example.projectjavaservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

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

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        // 1. Tìm user theo Email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with this email"));

        // 2. Tạo mã OTP 6 số ngẫu nhiên
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        // 3. Set thời gian hết hạn (5 phút)
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        // 4. Lưu OTP và Expiry vào DB
        user.setOtp(otp);
        user.setOtpExpiry(expiryTime);
        userRepository.save(user);

        // 5. Gửi Email chứa OTP sử dụng JavaMailSender
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Badminton Booking - Password Reset OTP");
        message.setText("Your OTP to reset password is: " + otp + "\nThis OTP will expire in 5 minutes.");

        mailSender.send(message); // GỬI ĐI!
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Tìm user theo Email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found with this email"));

        // 2. Kiểm tra xem user có yêu cầu OTP chưa
        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            throw new BadRequestException("No OTP request found. Please request a new OTP.");
        }

        // 3. Kiểm tra OTP khớp không
        if (!user.getOtp().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        // 4. Kiểm tra OTP hết hạn chưa
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // 5. Tất cả hợp lệ -> Đổi mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // 6. Xóa OTP trong DB đi để không dùng lại (Bảo mật)
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
    }
}