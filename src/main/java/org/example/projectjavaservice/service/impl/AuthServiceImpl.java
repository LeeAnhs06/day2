package org.example.projectjavaservice.service.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.Request.LoginRequest;
import org.example.projectjavaservice.dto.Response.LoginResponse;
import org.example.projectjavaservice.dto.Request.RefreshTokenRequest;
import org.example.projectjavaservice.exception.BadRequestException;
import org.example.projectjavaservice.security.CustomUserDetails;
import org.example.projectjavaservice.security.CustomUserDetailsService;
import org.example.projectjavaservice.security.JwtUtil;
import org.example.projectjavaservice.service.AuthService;
import org.example.projectjavaservice.service.RedisService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    // ĐÃ XÓA: private final TokenBlacklistRepository tokenBlacklistRepository;
    private final CustomUserDetailsService userDetailsService;
    private final RedisService redisService; // DÙNG REDIS SERVICE

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            throw new BadRequestException("Invalid or expired token");
        }

        // Kiểm tra xem token đã bị blacklist trên Redis chưa
        if (redisService.isTokenBlacklisted(token)) {
            return;
        }

        // TÍNH THỜI GIAN CÒN SỐNG CỦA TOKEN
        long now = System.currentTimeMillis();
        long expirationTime = claims.getExpiration().getTime();
        long remainingTimeInMillis = expirationTime - now;

        // Chỉ lưu vào Redis nếu token chưa hết hạn hoàn toàn
        if (remainingTimeInMillis > 0) {
            redisService.saveTokenToBlacklist(token, remainingTimeInMillis);
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        Claims claims = jwtUtil.parseToken(refreshToken);
        if (claims == null) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        // ĐỔI SANG REDIS: Kiểm tra xem refresh token này đã bị thu hồi chưa
        if (redisService.isTokenBlacklisted(refreshToken)) {
            throw new BadRequestException("Refresh token has been revoked");
        }

        String username = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        // ĐỔI SANG REDIS: Lưu cái RefreshToken cũ vào Redis để chống dùng lại (Replay Attack)
        // Tính thời gian sống còn lại của cái refreshToken cũ
        long now = System.currentTimeMillis();
        long expirationTime = claims.getExpiration().getTime();
        long remainingTimeInMillis = expirationTime - now;

        if (remainingTimeInMillis > 0) {
            redisService.saveTokenToBlacklist(refreshToken, remainingTimeInMillis);
        }

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}