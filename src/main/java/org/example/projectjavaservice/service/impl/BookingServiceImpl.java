package org.example.projectjavaservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.projectjavaservice.dto.Request.BookingRequest;
import org.example.projectjavaservice.dto.Response.BookingResponse;
import org.example.projectjavaservice.entity.Booking;
import org.example.projectjavaservice.entity.enums.BookingStatus;
import org.example.projectjavaservice.entity.Court;
import org.example.projectjavaservice.entity.TimeSlot;
import org.example.projectjavaservice.entity.User;
import org.example.projectjavaservice.exception.BadRequestException;
import org.example.projectjavaservice.exception.ConflictException;
import org.example.projectjavaservice.exception.NotFoundException;
import org.example.projectjavaservice.repository.BookingRepository;
import org.example.projectjavaservice.repository.CourtRepository;
import org.example.projectjavaservice.repository.TimeSlotRepository;
import org.example.projectjavaservice.repository.UserRepository;
import org.example.projectjavaservice.service.BookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Override
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new NotFoundException("Court not found"));

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new NotFoundException("Time slot not found"));

        if (request.getBookingDate().isEqual(LocalDate.now())) {
            LocalTime startTime = LocalTime.parse(timeSlot.getStartTime());
            if (startTime.isBefore(LocalTime.now())) {
                throw new BadRequestException("Cannot book a time slot in the past");
            }
        }

        boolean isConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotIdAndStatusIn(
                court.getId(),
                request.getBookingDate(),
                request.getTimeSlotId(),
                Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );

        if (isConflict) {
            throw new ConflictException("Court is already booked for this date and time slot");
        }
        Booking booking = Booking.builder()
                .user(user)
                .court(court)
                .bookingDate(request.getBookingDate())
                .timeSlotId(request.getTimeSlotId())
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

    @Override
    public BookingResponse approveBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

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

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToResponse(updatedBooking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        String timeSlotStr = "Unknown";
        if (booking.getTimeSlotId() != null) {
            timeSlotStr = timeSlotRepository.findById(booking.getTimeSlotId())
                    .map(ts -> ts.getStartTime() + "-" + ts.getEndTime())
                    .orElse("Unknown");
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .courtId(booking.getCourt().getId())
                .courtName(booking.getCourt().getName())
                .userId(booking.getUser().getId())
                .username(booking.getUser().getUsername())
                .bookingDate(booking.getBookingDate())
                .timeSlot(timeSlotStr)
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .build();
    }
}