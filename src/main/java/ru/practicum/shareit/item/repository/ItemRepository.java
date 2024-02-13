package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);
    Item update(Item item, Long id);
    Item findById(Long id);
    List<Item> findAll();
    List<Item> findAllByOwnerId(Long ownerId);
    List<Item> findBySubstring(String text);
}
