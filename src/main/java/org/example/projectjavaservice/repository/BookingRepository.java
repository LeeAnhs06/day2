package org.example.projectjavaservice.repository;

import org.example.projectjavaservice.entity.Booking;
import org.example.projectjavaservice.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByCourtIdAndBookingDate(Long courtId, LocalDate bookingDate);
    boolean existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
            Long courtId, LocalDate bookingDate, String timeSlot, List<BookingStatus> statuses);
}