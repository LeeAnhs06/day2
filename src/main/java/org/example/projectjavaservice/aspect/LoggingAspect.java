package org.example.projectjavaservice.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.example.projectjavaservice.dto.BookingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning(
            pointcut = "execution(* org.example.projectjavaservice.service.impl.BookingServiceImpl.createBooking(..))",
            returning = "result"
    )
    public void logBookingSuccess(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        BookingRequest request = (BookingRequest) args[1];

        log.info("[AUDIT - SUCCESS] Khách hàng ID {} đặt thành công Sân ID {} vào ngày {}, Khung giờ ID {}",
                userId,
                request.getCourtId(),
                request.getBookingDate(),
                request.getTimeSlotId());
    }

    @AfterThrowing(
            pointcut = "execution(* org.example.projectjavaservice.service.impl.BookingServiceImpl.createBooking(..))",
            throwing = "ex"
    )
    public void logBookingFailure(JoinPoint joinPoint, Exception ex) {
        Object[] args = joinPoint.getArgs();
        Long userId = (Long) args[0];
        BookingRequest request = (BookingRequest) args[1];

        log.error("[AUDIT - FAILED] Khách hàng ID {} cố gắng đặt Sân ID {} nhưng thất bại do: {}",
                userId,
                request.getCourtId(),
                ex.getMessage());
    }
}