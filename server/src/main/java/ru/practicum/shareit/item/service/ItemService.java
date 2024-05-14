package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long userId, Long itemId, ItemPatchDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    Page<ItemDto> getAllItemsWithBookingDetails(Long ownerId, Pageable pageable);

    Page<ItemDto> getItemsBySubstring(String text, Pageable pageable);

    boolean isItemPresent(long itemId);

    boolean isItemBelongOwner(long itemId, long ownerId);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long userId);
}
