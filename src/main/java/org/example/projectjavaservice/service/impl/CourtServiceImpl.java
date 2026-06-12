package org.example.projectjavaservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.CourtDetailResponse;
import org.example.projectjavaservice.dto.CourtResponse;
import org.example.projectjavaservice.dto.TimeSlotAvailabilityResponse;
import org.example.projectjavaservice.entity.BookingStatus;
import org.example.projectjavaservice.entity.Court;
import org.example.projectjavaservice.entity.TimeSlot;
import org.example.projectjavaservice.exception.NotFoundException;
import org.example.projectjavaservice.repository.BookingRepository;
import org.example.projectjavaservice.repository.CourtRepository;
import org.example.projectjavaservice.repository.TimeSlotRepository;
import org.example.projectjavaservice.service.CourtService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<CourtResponse> getAllCourts() {
        List<Court> courts = courtRepository.findByIsAvailableTrue();
        return courts.stream()
                .map(court -> CourtResponse.builder()
                        .id(court.getId())
                        .name(court.getName())
                        .location(court.getLocation())
                        .pricePerHour(court.getPricePerHour())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CourtDetailResponse getCourtDetailWithAvailability(Long courtId, LocalDate date) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new NotFoundException("Court not found"));

        List<TimeSlot> allTimeSlots = timeSlotRepository.findAll();

        List<TimeSlotAvailabilityResponse> slotResponses = allTimeSlots.stream()
                .map(slot -> {
                    boolean isBooked = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotIdAndStatusIn(
                            courtId,
                            date,
                            slot.getId(),
                            Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED)
                    );

                    return TimeSlotAvailabilityResponse.builder()
                            .timeSlotId(slot.getId())
                            .startTime(slot.getStartTime())
                            .endTime(slot.getEndTime())
                            .status(isBooked ? "BOOKED" : "AVAILABLE")
                            .build();
                })
                .collect(Collectors.toList());

        return CourtDetailResponse.builder()
                .id(court.getId())
                .name(court.getName())
                .location(court.getLocation())
                .pricePerHour(court.getPricePerHour())
                .timeSlots(slotResponses)
                .build();
    }
}