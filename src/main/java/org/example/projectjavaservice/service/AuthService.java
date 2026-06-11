package org.example.projectjavaservice.service;

import org.example.projectjavaservice.dto.LoginRequest;
import org.example.projectjavaservice.dto.LoginResponse;
import org.example.projectjavaservice.dto.RefreshTokenRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    LoginResponse refreshToken(RefreshTokenRequest request);
}