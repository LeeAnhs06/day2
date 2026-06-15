package org.example.projectjavaservice.service;

import org.example.projectjavaservice.dto.Request.CourtCreateRequest;
import org.example.projectjavaservice.dto.Request.TimeSlotCreateRequest;
import org.example.projectjavaservice.dto.Response.CourtDetailResponse;
import org.example.projectjavaservice.dto.Response.CourtResponse;
import org.example.projectjavaservice.dto.Response.TimeSlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface CourtService {
    List<CourtResponse> getAllCourts();
    CourtDetailResponse getCourtDetailWithAvailability(Long courtId, LocalDate date);
CourtResponse createCourt(CourtCreateRequest request);
    TimeSlotResponse createTimeSlot(TimeSlotCreateRequest request);
}