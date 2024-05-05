package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.PageableUtil;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking savedBooking = bookingService.createBooking(bookingDto, userId);
        return bookingMapper.toBookingDto(savedBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Booking approvedBooking = bookingService.approveBooking(bookingId, ownerId, approved);
        return bookingMapper.toBookingDto(approvedBooking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.getBookingById(bookingId, userId);
        return bookingMapper.toBookingDto(booking);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                   @Positive @RequestParam(defaultValue = "10") int size) {
        Sort sort = (state.equals("CURRENT")) ? Sort.by(Sort.Direction.ASC, "id") : Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageableUtil.createPageable(from, size, sort);
        Page<BookingDto> pageResult = bookingService.getAllBookingsByBooker(userId, state, pageable);
        return pageResult.getContent();
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageableUtil.createPageable(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<BookingDto> pageResult = bookingService.getAllBookingsByOwner(ownerId, state, pageable);
        return pageResult.getContent();
    }
}
