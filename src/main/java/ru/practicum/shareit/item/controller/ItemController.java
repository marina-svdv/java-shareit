package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        User user = userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, userId);
        Item savedItem = itemService.createItem(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item itemToUpdate = ItemMapper.toItem(itemDto, userId);
        Item updatedItem = itemService.updateItem(itemToUpdate, itemId, userId);
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        Item item = itemService.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> items = itemService.getAllItemsByOwner(userId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySubstring(@RequestParam String text) {
        List<Item> items = itemService.getItemsBySubstring(text);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
