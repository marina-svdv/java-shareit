package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto createBooking(BookingDto bookingdto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Item item = itemRepository.findById(bookingdto.getItemId())
                .orElseThrow(ItemNotFoundException::new);
        User owner = item.getOwner();

        Booking booking = bookingMapper.toBooking(bookingdto, owner, item, booker);
        if (!item.getAvailable()) {
            log.info("Item is not available");
            throw new ItemNotAvailableException("Item is not available");
        }
        if (item.getOwner().getId().equals(booking.getBooker().getId())) {
            log.info("Owner cannot book their own item");
            throw new UnauthorizedAccessException();
        }
        List<Booking> overlappingBookings = bookingRepository.findByItemIdAndEndAfterAndStartBefore(
                booking.getItem().getId(), booking.getStart(), booking.getEnd());
        if (!overlappingBookings.isEmpty()) {
            log.info("Item has overlapping bookings");
            throw new OverlappingBookingsException("Item has overlapping bookings");
        }
        booking.setItem(item);
        booking.setOwner(owner);
        booking.setBooker(booker);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException();
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new IllegalStateException("Only bookings in the WAITING status can be approved or rejected.");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);
        if (!booking.getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new UnauthorizedAccessException();
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public Page<BookingDto> getAllBookingsByBooker(Long bookerId, String state, Pageable pageable) {
        if (!userRepository.existsById(bookerId)) {
            throw new UserNotFoundException();
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findAllByBookerId(bookerId, pageable)
                        .map(bookingMapper::toBookingDto);
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING, pageable)
                        .map(bookingMapper::toBookingDto);
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.REJECTED, pageable)
                        .map(bookingMapper::toBookingDto);
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBefore(bookerId, now, pageable)
                        .map(bookingMapper::toBookingDto);
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfter(bookerId, now, pageable)
                        .map(bookingMapper::toBookingDto);
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, pageable)
                        .map(bookingMapper::toBookingDto);
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    @Override
    public Page<BookingDto> getAllBookingsByOwner(Long ownerId, String state, Pageable pageable) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException();
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findAllByOwnerId(ownerId, pageable)
                        .map(bookingMapper::toBookingDto);
            case "WAITING":
                return bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.WAITING, pageable)
                        .map(bookingMapper::toBookingDto);
            case "REJECTED":
                return bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED, pageable)
                        .map(bookingMapper::toBookingDto);
            case "PAST":
                return bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, now, pageable)
                        .map(bookingMapper::toBookingDto);
            case "FUTURE":
                return bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, now, pageable)
                        .map(bookingMapper::toBookingDto);
            case "CURRENT":
                return bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, pageable)
                        .map(bookingMapper::toBookingDto);
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
