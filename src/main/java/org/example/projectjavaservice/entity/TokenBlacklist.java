package org.example.projectjavaservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lưu chuỗi token bị thu hồi
    @Column(nullable = false, unique = true, length = 500)
    private String token;

    // Lưu thời gian token này hết hạn thực sự (để xóa dữ liệu rác sau này)
    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;
}