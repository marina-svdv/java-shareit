package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);
    Booking approveBooking(Long bookingId, Long ownerId, boolean approved);
    List<Booking> getAllBookingsForUser(Long userId);
}
