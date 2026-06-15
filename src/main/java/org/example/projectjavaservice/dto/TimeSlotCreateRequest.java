package org.example.projectjavaservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeSlotCreateRequest {
    @NotBlank(message = "Start time is required")
    private String startTime; // VD: "08:00"

    @NotBlank(message = "End time is required")
    private String endTime;   // VD: "09:00"
}