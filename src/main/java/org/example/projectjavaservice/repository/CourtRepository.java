package org.example.projectjavaservice.repository;

import org.example.projectjavaservice.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByIsAvailableTrue();
}