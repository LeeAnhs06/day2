package org.example.projectjavaservice.service;

import org.example.projectjavaservice.dto.CourtDetailResponse;
import org.example.projectjavaservice.dto.CourtResponse;

import java.time.LocalDate;
import java.util.List;

public interface CourtService {
    List<CourtResponse> getAllCourts();
    CourtDetailResponse getCourtDetailWithAvailability(Long courtId, LocalDate date);
}