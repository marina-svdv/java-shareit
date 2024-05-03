package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    Item createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    Page<ItemDto> getAllItemsWithBookingDetails(Long ownerId, Pageable pageable);

    Page<ItemDto> getItemsBySubstring(String text, Pageable pageable);

    boolean isItemPresent(long itemId);

    boolean isItemBelongOwner(long itemId, long ownerId);

    CommentDto addComment(Long itemId, CommentDto commentDto);
}
