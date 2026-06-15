package org.example.projectjavaservice.service;

import org.example.projectjavaservice.dto.Request.BookingRequest;
import org.example.projectjavaservice.dto.Response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(Long userId, BookingRequest request);
    BookingResponse getBookingById(Long id);
    List<BookingResponse> getBookingsByUserId(Long userId);
    void cancelBooking(Long bookingId);
        BookingResponse approveBooking(Long bookingId);
    BookingResponse rejectBooking(Long bookingId);
}