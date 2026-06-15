package org.example.projectjavaservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.Response.ApiResponse;
import org.example.projectjavaservice.dto.Response.CourtDetailResponse;
import org.example.projectjavaservice.dto.Response.CourtResponse;
import org.example.projectjavaservice.service.CourtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    // Xem danh sách sân
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourtResponse>>> getAllCourts() {
        List<CourtResponse> courts = courtService.getAllCourts();
        return ResponseEntity.ok(ApiResponse.<List<CourtResponse>>builder()
                .success(true)
                .message("Courts retrieved successfully")
                .data(courts)
                .build());
    }

    // Xem chi tiết sân + Tình trạng khung giờ
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourtDetailResponse>> getCourtDetail(
            @PathVariable Long id,
            @RequestParam LocalDate date) { // ?date=2025-06-15
        
        CourtDetailResponse courtDetail = courtService.getCourtDetailWithAvailability(id, date);
        return ResponseEntity.ok(ApiResponse.<CourtDetailResponse>builder()
                .success(true)
                .message("Court detail retrieved successfully")
                .data(courtDetail)
                .build());
    }
}