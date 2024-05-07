package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    @Test
    public void toItemRequestDtoShouldConvertToDto() {
        User user = new User(1L, "Requester User", "requester@example.com");
        Item item = new Item(1L, "Laptop", "Good laptop", true, user, null);
        ItemRequest itemRequest = new ItemRequest(1L, "Need a laptop", user,
                LocalDateTime.now(), List.of(item));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Laptop");
        itemDto.setDescription("Good laptop");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1L);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        ItemRequestDto dto = itemRequestMapper.toItemRequestDto(itemRequest);

        assertNotNull(dto);
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getRequester().getId(), dto.getRequesterId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
        assertFalse(dto.getItems().isEmpty());
        assertEquals(1, dto.getItems().size());
        assertEquals(itemDto, dto.getItems().get(0));
    }

    @Test
    public void toItemRequestShouldConvertFromDto() {
        ItemRequestDto dto = new ItemRequestDto(1L, "Need a laptop", 1L,
                LocalDateTime.now(), Collections.emptyList());

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(dto);

        assertNotNull(itemRequest);
        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
        assertEquals(dto.getCreated(), itemRequest.getCreated());
    }

    @Test
    public void toItemRequestDtoWithNullShouldReturnNull() {
        assertNull(itemRequestMapper.toItemRequestDto(null));
    }

    @Test
    public void toItemRequestWithNullShouldReturnNull() {
        assertNull(itemRequestMapper.toItemRequest(null));
    }
}
