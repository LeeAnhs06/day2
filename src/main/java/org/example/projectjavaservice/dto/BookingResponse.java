package org.example.projectjavaservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class BookingResponse {
    private Long id;
    private Long courtId;
    private String courtName;
    private Long userId;
    private String username;
    private LocalDate bookingDate;
    private String timeSlot;
    private BigDecimal totalPrice;
    private String status;
}