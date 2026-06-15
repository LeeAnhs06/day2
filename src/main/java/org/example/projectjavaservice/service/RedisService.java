package org.example.projectjavaservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    // Spring Boot tự động cung cấp RedisTemplate nhờ dependency
    private final RedisTemplate<String, Object> redisTemplate;

    // Lưu token vào danh sách đen (Blacklist) trên Redis
    // Kèm theo thời gian sống (TTL), hết hạn tự xóa khỏi RAM -> Cực kỳ tối ưu!
    public void saveTokenToBlacklist(String token, long expirationInMillis) {
        redisTemplate.opsForValue().set(token, "blacklisted", expirationInMillis, TimeUnit.MILLISECONDS);
    }

    // Kiểm tra xem token có nằm trong blacklist không
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}