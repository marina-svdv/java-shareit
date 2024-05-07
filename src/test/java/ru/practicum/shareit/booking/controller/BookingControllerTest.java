package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingMapper bookingMapper;

    @Test
    void createBookingShouldReturnBookingDtoWhenValidRequest() throws Exception {
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(startTime);
        bookingDto.setEnd(endTime);
        bookingDto.setStatus(Status.WAITING);
        Booking savedBooking = new Booking();
        savedBooking.setId(1L);

        when(bookingService.createBooking(any(BookingDto.class), eq(userId))).thenReturn(savedBooking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(bookingService).createBooking(any(BookingDto.class), eq(userId));
        verify(bookingMapper).toBookingDto(any(Booking.class));
    }

    @Test
    void approveBookingShouldChangeBookingStatusWhenValidRequest() throws Exception {
        Long bookingId = 1L;
        Long ownerId = 1L;
        boolean approved = true;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        Booking approvedBooking = new Booking();
        approvedBooking.setId(bookingId);

        when(bookingService.approveBooking(bookingId, ownerId, approved)).thenReturn(approvedBooking);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingService).approveBooking(bookingId, ownerId, approved);
        verify(bookingMapper).toBookingDto(any(Booking.class));
    }

    @Test
    void getBookingByIdShouldReturnBookingDtoWhenFound() throws Exception {
        long bookingId = 1L;
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);

        when(bookingService.getBookingById(bookingId, userId)).thenReturn(new Booking());
        when(bookingMapper.toBookingDto(any())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));

        verify(bookingService).getBookingById(bookingId, userId);
        verify(bookingMapper).toBookingDto(any());
    }

    @Test
    void getAllBookingsByBookerShouldReturnBookings() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        String state = "ALL";
        List<Booking> bookings = List.of(new Booking(), new Booking());
        List<BookingDto> bookingDtos = bookings.stream()
                .map(booking -> {
                    BookingDto dto = new BookingDto();
                    dto.setId(booking.getId());
                    return dto;
                })
                .collect(Collectors.toList());
        Page<BookingDto> pageDto = new PageImpl<>(bookingDtos);

        when(bookingService.getAllBookingsByBooker(eq(userId), eq(state), any(Pageable.class)))
                .thenReturn(pageDto);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingDtos.size()));

        verify(bookingService).getAllBookingsByBooker(eq(userId), eq(state), any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerShouldReturnBookings() throws Exception {
        Long ownerId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "id"));
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        List<BookingDto> bookingDtos = List.of(bookingDto1, bookingDto2);
        Page<BookingDto> page = new PageImpl<>(bookingDtos, pageable, bookingDtos.size());

        when(bookingService.getAllBookingsByOwner(eq(ownerId), eq(state), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$.length()").value(bookingDtos.size()));

        verify(bookingService).getAllBookingsByOwner(eq(ownerId), eq(state), any(Pageable.class));
    }

    @Test
    void getAllBookingsByOwnerShouldHandleInvalidPagination() throws Exception {
        Long ownerId = 1L;
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}
