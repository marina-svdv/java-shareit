package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 0;

    @Override
    public Item create(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item updatedItem, Long itemId) {
        Item existingItem = findById(itemId);
        existingItem.setName(updatedItem.getName() != null ? updatedItem.getName() : existingItem.getName());
        existingItem.setDescription(updatedItem.getDescription() != null ?
                updatedItem.getDescription() : existingItem.getDescription());
        existingItem.setAvailable(updatedItem.getAvailable() != null ?
                updatedItem.getAvailable() : existingItem.getAvailable());
        items.put(itemId, existingItem);
        return existingItem;
    }

    @Override
    public Item findById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> ownerId.equals(item.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findBySubstring(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList(); // Возвращаем пустой список, если строка пуста
        }
        String query = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(query) ||
                        item.getDescription().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    public boolean isItemPresent(long itemId) {
        return (items.get(itemId) != null);
    }

    public boolean isItemBelongOwner(long itemId, long ownerId) {
        return items.get(itemId).getOwnerId().equals(ownerId);
    }
}
