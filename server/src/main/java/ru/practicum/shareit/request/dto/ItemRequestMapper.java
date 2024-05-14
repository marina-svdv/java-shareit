package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequesterId(itemRequest.getRequester().getId());
        dto.setCreated(itemRequest.getCreated());
        List<Item> items = itemRequest.getItems();
        if (items != null) {
            List<ItemDto> itemDtos = items.stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        } else {
            dto.setItems(Collections.emptyList());
        }
        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            return null;
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now());
        return itemRequest;
    }
}
