package org.example.projectjavaservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TimeSlotAvailabilityResponse {
    private Long timeSlotId;
    private String startTime;
    private String endTime;
    private String status;
}