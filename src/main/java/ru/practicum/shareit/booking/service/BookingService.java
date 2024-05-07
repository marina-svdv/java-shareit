package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {

    Booking createBooking(BookingDto bookingDto, Long userID);

    Booking getBookingById(Long bookingId, Long userId);

    Page<BookingDto> getAllBookingsByBooker(Long bookerId, String state, Pageable pageable);

    Page<BookingDto> getAllBookingsByOwner(Long ownerId, String state, Pageable pageable);

    Booking approveBooking(Long bookingId, Long ownerId, boolean approved);
}
