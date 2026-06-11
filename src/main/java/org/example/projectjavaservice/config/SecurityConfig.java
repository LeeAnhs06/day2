package org.example.projectjavaservice.config;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Bắt lỗi 401 chuẩn chỉnh
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Unauthorized");
                }))
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC: Ai cũng vào được (Đăng ký, Login, Refresh)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // 2. ADMIN: Chỉ Quản trị hệ thống (Quản lý User)
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 3. MANAGER: Chủ sân (Duyệt lịch, Quản lý sân, Upload ảnh)
                        // (Cho phép cả Admin thăm dò khu vực Manager)
                        .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")

                        // 4. CUSTOMER: Khách hàng (Đặt sân, Xem lịch sử của mình)
                        // (Cho phép cả Admin và Manager thăm dò khu vực Customer)
                        .requestMatchers("/api/v1/customer/**").hasAnyRole("CUSTOMER", "MANAGER", "ADMIN")

                        // Các request không khớp rule nào -> Phải đăng nhập
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}