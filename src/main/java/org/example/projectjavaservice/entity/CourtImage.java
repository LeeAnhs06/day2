package org.example.projectjavaservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "court_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // Lưu đường dẫn ảnh trên mây

    @ManyToOne
    @JoinColumn(name = "court_id", nullable = false)
    private Court court; // Khóa ngoại liên kết với sân
}