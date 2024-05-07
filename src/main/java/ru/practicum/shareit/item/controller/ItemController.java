package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.PageableUtil;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
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
    public List<ItemDto> getAllItemsByOwner(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                            @Positive @RequestParam(defaultValue = "10") int size,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        Pageable pageable = PageableUtil.createPageable(from, size, Sort.by("name").descending());
        Page<ItemDto> pageResult = itemService.getAllItemsWithBookingDetails(userId, pageable);
        return pageResult.getContent();
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySubstring(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageableUtil.createPageable(from, size, Sort.by("id").ascending());
        Page<ItemDto> pageResult = itemService.getItemsBySubstring(text, pageable);
        return pageResult.getContent();
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        commentDto.setAuthorId(userId);
        CommentDto response = itemService.addComment(itemId, commentDto);
        log.info("Returning from addComment: {}", response);
        return response;
    }
}
