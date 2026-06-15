package org.example.projectjavaservice.service;

import org.example.projectjavaservice.dto.Request.LoginRequest;
import org.example.projectjavaservice.dto.Response.LoginResponse;
import org.example.projectjavaservice.dto.Request.RefreshTokenRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
    LoginResponse refreshToken(RefreshTokenRequest request);
}