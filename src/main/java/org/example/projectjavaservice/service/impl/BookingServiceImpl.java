package org.example.projectjavaservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.BookingRequest;
import org.example.projectjavaservice.dto.BookingResponse;
import org.example.projectjavaservice.entity.Booking;
import org.example.projectjavaservice.entity.BookingStatus;
import org.example.projectjavaservice.entity.Court;
import org.example.projectjavaservice.entity.User;
import org.example.projectjavaservice.exception.BadRequestException;
import org.example.projectjavaservice.exception.ConflictException;
import org.example.projectjavaservice.exception.NotFoundException;
import org.example.projectjavaservice.repository.BookingRepository;
import org.example.projectjavaservice.repository.CourtRepository;
import org.example.projectjavaservice.repository.UserRepository;
import org.example.projectjavaservice.service.BookingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;

    @Override
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new NotFoundException("Court not found"));




        // kiểm tra xem sân này có đang được đặt ở khung giờ này không
        boolean isConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
                court.getId(),
                request.getBookingDate(),
                request.getTimeSlot(),
                Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );

        if (isConflict) {
            throw new ConflictException("Court is already booked for this date and time slot");
        }

        Booking booking = Booking.builder()
                .user(user)
                .court(court)
                .bookingDate(request.getBookingDate())
                .timeSlot(request.getTimeSlot())
                .totalPrice(court.getPricePerHour())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .courtId(booking.getCourt().getId())
                .courtName(booking.getCourt().getName())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .bookingDate(booking.getBookingDate())
                .timeSlot(booking.getTimeSlot())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .build();
    }

        @Override
    public BookingResponse approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Chỉ được duyệt khi trạng thái đang là PENDING (Chờ duyệt)
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be approved");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToResponse(updatedBooking);
    }

    @Override
    public BookingResponse rejectBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        // Chỉ được từ chối khi trạng thái đang là PENDING
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToResponse(updatedBooking);
    }
}