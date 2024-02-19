package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item createItem(Item item) {
        return itemRepository.create(item);
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        if (!isItemPresent(itemId) || !isItemBelongOwner(itemId, userId)) {
            throw new UserNotFoundException();
        }
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

    public boolean isItemPresent(long itemId) {
        boolean isPresent = itemRepository.isItemPresent(itemId);
        if (isPresent) {
            log.info("Found item with id: {}", itemId);
        } else {
            log.info("Not found item with id {}", itemId);
        }
        return isPresent;
    }

    public boolean isItemBelongOwner(long itemId, long ownerId) {
        boolean isBelong = itemRepository.isItemBelongOwner(itemId, ownerId);
        if (isBelong) {
            log.info("Item with ID: {} belongs to the user with ID: {}", itemId, ownerId);
        } else {
            log.info("Item with ID: {} does not belongs to the user with ID: {}", itemId, ownerId);
        }
        return isBelong;
    }
}
