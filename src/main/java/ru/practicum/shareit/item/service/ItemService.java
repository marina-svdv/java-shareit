package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item);

    Item updateItem(Item item, Long ownerId, Long userId) throws IllegalArgumentException;

    Item getItemById(Long itemId) throws IllegalArgumentException;

    List<Item> getAllItemsByOwner(Long userId);

    List<Item> getItemsBySubstring(String text);

    boolean isItemPresent(long itemId);

    boolean isItemBelongOwner(long itemId, long ownerId);
}
