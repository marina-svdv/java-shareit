package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<Item> getAllItemsByOwner(Long userId);

    List<ItemDto> getItemsWithBookingDetails(Long ownerId);

    List<Item> getItemsBySubstring(String text);

    boolean isItemPresent(long itemId);

    boolean isItemBelongOwner(long itemId, long ownerId);

    CommentDto addComment(Long itemId, CommentDto commentDto);
}
