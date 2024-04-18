package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item savedItem = itemService.createItem(itemDto, userId);
        return itemMapper.toItemDto(savedItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemPatchDto itemPatchDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto itemToUpdate = itemService.getItemById(itemId, userId);
        ItemDto updatedItem = itemMapper.toItemDto(itemPatchDto, itemToUpdate);
        return itemService.updateItem(updatedItem, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsWithBookingDetails(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySubstring(@RequestParam String text) {
        List<Item> items = itemService.getItemsBySubstring(text);
        return items.stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        commentDto.setAuthorId(userId);
        return itemService.addComment(itemId, commentDto);
    }
}
