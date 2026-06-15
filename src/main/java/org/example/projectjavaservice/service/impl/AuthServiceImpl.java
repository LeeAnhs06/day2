package org.example.projectjavaservice.service.impl;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.Request.LoginRequest;
import org.example.projectjavaservice.dto.Response.LoginResponse;
import org.example.projectjavaservice.dto.Request.RefreshTokenRequest;
import org.example.projectjavaservice.entity.TokenBlacklist;
import org.example.projectjavaservice.exception.BadRequestException;
import org.example.projectjavaservice.repository.TokenBlacklistRepository;
import org.example.projectjavaservice.security.CustomUserDetails;
import org.example.projectjavaservice.security.CustomUserDetailsService;
import org.example.projectjavaservice.security.JwtUtil;
import org.example.projectjavaservice.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final CustomUserDetailsService userDetailsService;

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

        if (tokenBlacklistRepository.existsByToken(token)) {
            return;
        }

        Instant expiredAt = claims.getExpiration().toInstant();

        TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();

        tokenBlacklistRepository.save(blacklistEntry);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        Claims claims = jwtUtil.parseToken(refreshToken);
        if (claims == null) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        if (tokenBlacklistRepository.existsByToken(refreshToken)) {
            throw new BadRequestException("Refresh token has been revoked");
        }

        String username = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        Instant expiredAt = claims.getExpiration().toInstant();
        TokenBlacklist oldRefreshToken = TokenBlacklist.builder()
                .token(refreshToken)
                .expiredAt(expiredAt)
                .build();
        tokenBlacklistRepository.save(oldRefreshToken);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}