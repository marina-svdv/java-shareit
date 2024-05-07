package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {

    @InjectMocks
    private BookingMapper bookingMapper;

    @Test
    public void testToBookingDto() {
        User user = new User(1L, "User One", "user.one@example.com");
        User booker = new User(2L, "User Two", "user.two@example.com");
        Item item = new Item(1L, "Item One", "Description", true, user, null);
        Booking booking = new Booking(1L, item, user, booker, LocalDateTime.now(), LocalDateTime.now().plusDays(1), Status.APPROVED);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertNotNull(bookingDto);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItemId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    public void testToBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setOwnerId(1L);
        bookingDto.setBookerId(2L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setStatus(Status.APPROVED);

        User owner = new User(1L, "User One", "user.one@example.com");
        User booker = new User(2L, "User Two", "user.two@example.com");
        Item item = new Item(1L, "Item One", "Description", true, owner, null);

        Booking booking = bookingMapper.toBooking(bookingDto, owner, item, booker);

        assertNotNull(booking);
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getItemId(), booking.getItem().getId());
        assertEquals(bookingDto.getBookerId(), booking.getBooker().getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(bookingDto.getStatus(), booking.getStatus());
    }

    @Test
    public void testToBookingDtoWithNullBooking() {
        assertNull(bookingMapper.toBookingDto(null));
    }

    @Test
    public void testToBookingWithNullBookingDto() {
        assertNull(bookingMapper.toBooking(null, null, null, null));
    }
}
