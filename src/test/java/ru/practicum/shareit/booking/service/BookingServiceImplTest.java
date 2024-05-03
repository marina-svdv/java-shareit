package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingWithValidInputsShouldReturnBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(1L);

        User booker = new User(1L, "Booker", "booker@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setOwner(owner);
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(any(), any(), any(), any())).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking savedBooking = bookingService.createBooking(bookingDto, 1L);

        assertNotNull(savedBooking);
        assertEquals(Status.WAITING, savedBooking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBookingWithUnavailableItemShouldThrowException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);

        User booker = new User(1L, "Booker", "booker@example.com");
        Item item = new Item();
        item.setAvailable(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class, () -> bookingService.createBooking(bookingDto, 1L));
    }

    @Test
    void approveBookingWithValidInputsShouldChangeStatus() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        booking.setItem(new Item());
        booking.getItem().setOwner(new User(ownerId, "Owner", "owner@example.com"));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking updatedBooking = bookingService.approveBooking(bookingId, ownerId, true);

        assertNotNull(updatedBooking);
        assertEquals(Status.APPROVED, updatedBooking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBookingWhenNoOwnerShouldThrowUnauthorizedAccessException() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setOwner(new User(2L, "Other Owner", "other@example.com"));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedAccessException.class, () -> bookingService.approveBooking(bookingId, ownerId, true));
    }

    @Test
    void approveBookingWhenStatusNotWaitingShouldThrowIllegalStateException() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setItem(new Item());
        booking.getItem().setOwner(new User(ownerId, "Owner", "owner@example.com"));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () -> bookingService.approveBooking(bookingId, ownerId, true));
    }

    @Test
    void getBookingByIdWithValidIdAndValidUserShouldReturnBookingDto() {
        Long bookingId = 1L;
        Long userId = 1L;
        Booking booking = new Booking();
        booking.setBooker(new User(userId, "Booker", "booker@example.com"));
        booking.setOwner(new User(2L, "Owner", "owner@example.com"));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(bookingId, userId);

        assertNotNull(result);
        assertSame(booking, result);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingByIdWithInvalidUserShouldThrowUnauthorizedAccessException() {
        Long bookingId = 1L;
        Long userId = 1L;
        Booking booking = new Booking();
        booking.setBooker(new User(2L, "Booker", "booker@example.com"));
        booking.setOwner(new User(3L, "Owner", "owner@example.com"));

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedAccessException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getBookingByIdWithNonExistentIdShouldThrowBookingNotFoundException() {
        Long bookingId = 1L;
        Long userId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getAllBookingsByBookerWithValidParamsShouldReturnPageOfBookingDtos() {
        Long bookerId = 1L;
        int page = 0;
        int size = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageable = PageRequest.of(page, size, sort);
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Page<Booking> bookings = new PageImpl<>(Arrays.asList(booking1, booking2));

        when(userRepository.existsById(bookerId)).thenReturn(true); // Ensures the user exists
        when(bookingRepository.findAllByBookerId(bookerId, pageable)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(booking1)).thenReturn(new BookingDto());
        when(bookingMapper.toBookingDto(booking2)).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByBooker(bookerId, "ALL", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(bookingRepository).findAllByBookerId(bookerId, pageable);
        verify(userRepository).existsById(bookerId);
    }

    @Test
    void getAllBookingsByBookerWithFutureStateShouldReturnFutureBookings() {
        Long bookerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByBooker(bookerId, "FUTURE", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getAllBookingsByBookerShouldThrowUserNotFoundExceptionIfUserDoesNotExist() {
        Long bookerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        when(userRepository.existsById(bookerId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingsByBooker(bookerId, "ALL", pageable));
    }

    @Test
    void getAllBookingsByBookerWithWaitingStateShouldReturnWaitingBookings() {
        Long bookerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        booking1.setStatus(Status.WAITING);
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStatus(bookerId, Status.WAITING, pageable)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(any())).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByBooker(bookerId, "WAITING", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByBookerIdAndStatus(bookerId, Status.WAITING, pageable);
    }

    @Test
    void getAllBookingsByBookerWithPastStateShouldReturnPastBookings() {
        Long bookerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingDto(any())).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByBooker(bookerId, "PAST", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getAllBookingsByBookerWithCurrentStateShouldReturnCurrentBookings() {
        Long bookerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(bookerId)).thenReturn(true);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(eq(bookerId), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookings);
        when(bookingMapper.toBookingDto(any())).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByBooker(bookerId, "CURRENT", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByBookerIdAndStartBeforeAndEndAfter(eq(bookerId), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getAllBookingsByOwnerWithWaitingStateShouldReturnWaitingBookings() {
        Long ownerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        booking1.setStatus(Status.WAITING);
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.WAITING, pageable)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(any())).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByOwner(ownerId, "WAITING", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByOwnerIdAndStatus(ownerId, Status.WAITING, pageable);
    }


    @Test
    void getAllBookingsByOwnerWithRejectedStateShouldReturnRejectedBookings() {
        Long ownerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED, pageable)).thenReturn(bookings);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByOwner(ownerId, "REJECTED", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByOwnerIdAndStatus(ownerId, Status.REJECTED, pageable);
    }

    @Test
    void getAllBookingsByOwnerShouldThrowUserNotFoundExceptionIfUserDoesNotExist() {
        Long ownerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());

        when(userRepository.existsById(ownerId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingsByOwner(ownerId, "ALL", pageable));
    }

    @Test
    void getAllBookingsByOwnerWithPastStateShouldReturnPastBookings() {
        Long ownerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByOwnerIdAndEndBefore(eq(ownerId), any(LocalDateTime.class),
                eq(pageable))).thenReturn(bookings);
        when(bookingMapper.toBookingDto(any())).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByOwner(ownerId, "PAST", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByOwnerIdAndEndBefore(eq(ownerId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void getAllBookingsByOwnerWithCurrentStateShouldReturnCurrentBookings() {
        Long ownerId = 1L;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Booking booking1 = new Booking();
        Page<Booking> bookings = new PageImpl<>(List.of(booking1));

        when(userRepository.existsById(ownerId)).thenReturn(true);
        when(bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(eq(ownerId), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);
        when(bookingMapper.toBookingDto(any())).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getAllBookingsByOwner(ownerId, "CURRENT", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(bookingRepository).findAllByOwnerIdAndStartBeforeAndEndAfter(eq(ownerId), any(LocalDateTime.class),
                any(LocalDateTime.class), eq(pageable));
    }
}
