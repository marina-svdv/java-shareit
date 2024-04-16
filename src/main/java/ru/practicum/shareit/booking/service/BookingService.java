package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingDto bookingDto, Long userID);
    Booking getBookingById(Long bookingId, Long userId);
    List<Booking> getAllBookingsByBooker(Long bookerId, String state);
    List<Booking> getAllBookingsByOwner(Long ownerId, String state);
    Booking approveBooking(Long bookingId, Long ownerId, boolean approved);
}
