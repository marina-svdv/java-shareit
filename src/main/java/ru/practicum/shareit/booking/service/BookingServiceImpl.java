package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Override
    public Booking createBooking(Booking booking) {
        return null;
    }

    @Override
    public Booking approveBooking(Long bookingId, Long ownerId, boolean approved) {
        return null;
    }

    @Override
    public List<Booking> getAllBookingsForUser(Long userId) {
        return null;
    }
}
