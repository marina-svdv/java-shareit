package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.PageableUtil;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        Sort sort = (state.equals("CURRENT")) ? Sort.by(Sort.Direction.ASC, "id") : Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageableUtil.createPageable(from, size, sort);
        Page<BookingDto> pageResult = bookingService.getAllBookingsByBooker(userId, state, pageable);
        return pageResult.getContent();
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageableUtil.createPageable(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<BookingDto> pageResult = bookingService.getAllBookingsByOwner(ownerId, state, pageable);
        return pageResult.getContent();
    }
}
