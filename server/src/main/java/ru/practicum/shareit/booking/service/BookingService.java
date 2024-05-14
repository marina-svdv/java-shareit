package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long userID);

    BookingDto getBookingById(Long bookingId, Long userId);

    Page<BookingDto> getAllBookingsByBooker(Long bookerId, String state, Pageable pageable);

    Page<BookingDto> getAllBookingsByOwner(Long ownerId, String state, Pageable pageable);

    BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved);
}
