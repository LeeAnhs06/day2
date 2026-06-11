package org.example.projectjavaservice.repository;

import org.example.projectjavaservice.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    
    // Hàm check xem token này có nằm trong blacklist chưa
    boolean existsByToken(String token);
}