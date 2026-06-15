package org.example.projectjavaservice.dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CourtResponse {
    private Long id;
    private String name;
    private String location;
    private BigDecimal pricePerHour;
}