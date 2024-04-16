package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@RequiredArgsConstructor
@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        itemDto.setAvailable(booking.getItem().getAvailable());
        itemDto.setOwnerId(booking.getItem().getOwner().getId());
        dto.setItemId(booking.getItem().getId());
        dto.setItem(itemDto);
        dto.setOwnerId(booking.getOwner().getId());
        dto.setBookerId(booking.getBooker().getId());
        UserDto bookerDto = new UserDto();
        bookerDto.setId(booking.getBooker().getId());
        dto.setBooker(bookerDto);
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public Booking toBooking(BookingDto bookingDto, User owner, Item item, User booker) {
        if (bookingDto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setItem(item);
        booking.setOwner(owner);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus() != null ? bookingDto.getStatus() : Status.WAITING);
        return booking;
    }
}
