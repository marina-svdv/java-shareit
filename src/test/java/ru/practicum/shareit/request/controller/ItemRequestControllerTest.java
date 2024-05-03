package ru.practicum.shareit.request.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void addItemRequestShouldReturnItemRequestDtoWhenValidRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null,
                "Need a laptop", 1L, null, null);
        when(itemRequestService.addItemRequest(any(ItemRequestDto.class), eq(1L))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Need a laptop"));

        verify(itemRequestService).addItemRequest(any(ItemRequestDto.class), eq(1L));
    }

    @Test
    void addItemRequestShouldReturnBadRequestWhenInvalidData() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null,
                "", 1L, null, null);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).addItemRequest(any(ItemRequestDto.class), anyLong());
    }

    @Test
    void getItemRequestByIdShouldReturnItemRequestDto() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto(requestId,
                "Need a camera", userId, null, null);
        when(itemRequestService.getItemRequestById(userId, requestId)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a camera"));

        verify(itemRequestService).getItemRequestById(userId, requestId);
    }

    @Test
    void getItemRequestByIdShouldReturnNotFoundWhenRequestDoesNotExist() throws Exception {
        Long userId = 1L;
        Long requestId = 999L;
        when(itemRequestService.getItemRequestById(userId, requestId)).thenThrow(new ItemRequestNotFoundException());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getItemRequestById(userId, requestId);
    }

    @Test
    void getItemRequestsByUserShouldReturnListOfItemRequests() throws Exception {
        Long userId = 1L;
        List<ItemRequestDto> itemRequestDtos = List.of(
                new ItemRequestDto(1L, "Need a camera", userId, null, null),
                new ItemRequestDto(2L, "Looking for a laptop", userId, null, null)
        );
        when(itemRequestService.getItemRequestsByUser(userId)).thenReturn(itemRequestDtos);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a camera"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Looking for a laptop"));

        verify(itemRequestService).getItemRequestsByUser(userId);
    }

    @Test
    void getItemRequestsByUserShouldReturnEmptyListWhenNoRequests() throws Exception {
        Long userId = 1L;
        when(itemRequestService.getItemRequestsByUser(userId)).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemRequestService).getItemRequestsByUser(userId);
    }

    @Test
    void getAllItemRequestsShouldReturnPagedItemRequests() throws Exception {
        Long userId = 1L;
        int from = 0, size = 10;
        List<ItemRequestDto> itemRequestDtos = List.of(
                new ItemRequestDto(1L, "Need a camera", 2L, null, null),
                new ItemRequestDto(2L, "Looking for a laptop", 3L, null, null)
        );
        Page<ItemRequestDto> page = new PageImpl<>(itemRequestDtos);

        when(itemRequestService.getAllItemRequests(eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a camera"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Looking for a laptop"));

        verify(itemRequestService).getAllItemRequests(eq(userId), any(Pageable.class));
    }

    @Test
    void getAllItemRequestsShouldHandlePagination() throws Exception {
        Long userId = 1L;
        int from = 0, size = 1;
        List<ItemRequestDto> itemRequestDtos = List.of(new ItemRequestDto(1L,
                "Need a camera", 2L, null, null));
        Page<ItemRequestDto> page = new PageImpl<>(itemRequestDtos);

        when(itemRequestService.getAllItemRequests(eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a camera"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService).getAllItemRequests(eq(userId), any(Pageable.class));
    }
}
