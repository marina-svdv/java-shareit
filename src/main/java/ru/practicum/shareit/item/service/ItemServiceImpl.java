package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidCommentException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Override
    public Item createItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Item item = itemMapper.toItem(itemDto, owner);
        return itemRepository.save(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        if (!isItemPresent(itemId)) {
            throw new ItemNotFoundException();
        }
        if (!isItemBelongOwner(itemId, userId)) {
            throw new UnauthorizedAccessException();
        }
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);
        boolean isOwner = item.getOwner().getId().equals(userId);

        LocalDateTime now = LocalDateTime.now();
        log.info(now.toString());
        BookingDto lastBooking = null;
        BookingDto nextBooking = null;
        if (isOwner) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(item.getId(), Status.APPROVED, now)
                    .map(bookingMapper::toBookingDto).orElse(null);
            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), Status.APPROVED, now)
                    .map(bookingMapper::toBookingDto).orElse(null);
        }
        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        return itemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<Item> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public List<ItemDto> getItemsWithBookingDetails(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, Sort.by(Sort.Direction.ASC, "id"));
        LocalDateTime now = LocalDateTime.now();
        return items.stream().map(item -> {
            ItemDto itemDto = itemMapper.toItemDto(item);
            bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                            item.getId(), Status.APPROVED, now)
                    .ifPresent(booking ->
                            itemDto.setLastBooking(bookingMapper.toBookingDto(booking))
                    );
            bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), Status.APPROVED, now)
                    .ifPresent(booking ->
                            itemDto.setNextBooking(bookingMapper.toBookingDto(booking))
                    );
            return itemDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsBySubstring(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findBySubstring(text).stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    public boolean isItemPresent(long itemId) {
        boolean isPresent = itemRepository.existsById(itemId);
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

    public CommentDto addComment(Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);
        User user = userRepository.findById(commentDto.getAuthorId())
                .orElseThrow(UserNotFoundException::new);
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(commentDto.getAuthorId(), itemId, LocalDateTime.now())) {
            throw new InvalidCommentException();
        }
        Comment comment = commentMapper.toComment(commentDto, item, user);
        CommentDto newComment = commentMapper.toCommentDto(commentRepository.save(comment));
        newComment.setAuthorName(user.getName());
        return newComment;
    }
}
