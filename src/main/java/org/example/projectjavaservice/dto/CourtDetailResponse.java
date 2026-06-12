package org.example.projectjavaservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CourtDetailResponse {
    private Long id;
    private String name;
    private String location;
    private BigDecimal pricePerHour;
    private List<TimeSlotAvailabilityResponse> timeSlots;
}