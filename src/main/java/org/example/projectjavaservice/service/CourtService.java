package org.example.projectjavaservice.service;

import org.example.projectjavaservice.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface CourtService {
    List<CourtResponse> getAllCourts();
    CourtDetailResponse getCourtDetailWithAvailability(Long courtId, LocalDate date);
CourtResponse createCourt(CourtCreateRequest request);
    TimeSlotResponse createTimeSlot(TimeSlotCreateRequest request);
}