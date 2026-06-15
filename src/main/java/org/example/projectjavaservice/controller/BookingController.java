package org.example.projectjavaservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.Response.ApiResponse;
import org.example.projectjavaservice.dto.Request.BookingRequest;
import org.example.projectjavaservice.dto.Response.BookingResponse;
import org.example.projectjavaservice.security.CustomUserDetails;
import org.example.projectjavaservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    //Đặt sân & Xem lịch sử

    @PostMapping("/api/v1/customer/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        BookingResponse booking = bookingService.createBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BookingResponse>builder()
                        .success(true)
                        .message("Booking created successfully")
                        .data(booking)
                        .build());
    }

    @GetMapping("/api/v1/customer/bookings/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(Authentication authentication) {
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();

        List<BookingResponse> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.<List<BookingResponse>>builder()
                .success(true)
                .message("Bookings retrieved successfully")
                .data(bookings)
                .build());
    }


    // Duyệt & Từ chối lịch

    @PutMapping("/api/v1/manager/bookings/{id}/approve")
    public ResponseEntity<ApiResponse<BookingResponse>> approveBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.approveBooking(id);
        return ResponseEntity.ok(ApiResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking approved successfully")
                .data(booking)
                .build());
    }

    @PutMapping("/api/v1/manager/bookings/{id}/reject")
    public ResponseEntity<ApiResponse<BookingResponse>> rejectBooking(@PathVariable Long id) {
        BookingResponse booking = bookingService.rejectBooking(id);
        return ResponseEntity.ok(ApiResponse.<BookingResponse>builder()
                .success(true)
                .message("Booking rejected successfully")
                .data(booking)
                .build());
    }
}