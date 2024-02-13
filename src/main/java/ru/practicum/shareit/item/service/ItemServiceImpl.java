package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item createItem(Item item) {
        return itemRepository.create(item);
    }

    @Override
    public Item updateItem(Item item, Long itemId) {
        return itemRepository.update(item, itemId);
    }

    @Override
    public Item getItemById(Long itemId) throws IllegalArgumentException {
        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null) {
            throw new IllegalArgumentException("Item with id " + itemId + " not found.");
        }
        return existingItem;
    }

    @Override
    public List<Item> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public List<Item> getItemsBySubstring(String text) {
        return itemRepository.findBySubstring(text).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
