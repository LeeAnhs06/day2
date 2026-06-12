package org.example.projectjavaservice.repository;

import org.example.projectjavaservice.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
}