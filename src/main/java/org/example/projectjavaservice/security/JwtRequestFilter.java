package org.example.projectjavaservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.service.RedisService; // ĐÃ ĐỔI IMPORT
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final RedisService redisService; // ĐÃ ĐỔI TỪ REPOSITORY SANG REDIS SERVICE

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtUtil.parseToken(jwt);

            if (claims != null) {
                String username = claims.getSubject();
                Long tokenId = claims.get("userId", Long.class);

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                Long dbUserId = ((CustomUserDetails) userDetails).getId();

                boolean isIdMatch = tokenId != null && tokenId.equals(dbUserId);
                boolean isAccountActive = userDetails.isEnabled();

                // ---------------------------------------------------------
                // KIỂM TRA BLACKLIST BẰNG REDIS (Siêu nhanh)
                // Đã đổi hàm kiểm tra từ DB sang Redis
                boolean isTokenRevoked = redisService.isTokenBlacklisted(jwt);
                if (isTokenRevoked) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token has been revoked");
                    return; // Dừng request ngay lập tức
                }
                if (isIdMatch && isAccountActive) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}