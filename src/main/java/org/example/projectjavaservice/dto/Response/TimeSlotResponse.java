package org.example.projectjavaservice.dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TimeSlotResponse {
    private Long id;
    private String startTime;
    private String endTime;
}