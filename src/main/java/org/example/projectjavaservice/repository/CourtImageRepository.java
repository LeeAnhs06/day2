package org.example.projectjavaservice.repository;

import org.example.projectjavaservice.entity.CourtImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtImageRepository extends JpaRepository<CourtImage, Long> {
}