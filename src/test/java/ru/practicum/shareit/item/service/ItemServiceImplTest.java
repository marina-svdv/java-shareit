package ru.practicum.shareit.item.service;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void testCreateItemSuccess() {
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(1L);
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        ItemRequest itemRequest = new ItemRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(item);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        Item result = itemService.createItem(itemDto, 1L);

        assertNotNull(result);
        assertEquals(itemRequest, result.getRequest());
        verify(itemRepository).save(item);
    }

    @Test
    public void testCreateItemWithNonExistentUser() {
        ItemDto itemDto = new ItemDto();
        when(userRepository.findById(anyLong())).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> {
            itemService.createItem(itemDto, 1L);
        });
    }

    @Test
    public void testCreateItemWithNonExistentItemRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(1L);

        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenThrow(ItemRequestNotFoundException.class);

        assertThrows(ItemRequestNotFoundException.class, () -> {
            itemService.createItem(itemDto, 1L);
        });
    }

    @Test
    public void testUpdateItemSuccess() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        User user = new User();

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(itemRepository.isItemBelongOwner(itemId, userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(itemDto, user)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.updateItem(itemDto, itemId, userId);

        assertNotNull(result);
        verify(itemRepository).save(item);
    }

    @Test
    public void testUpdateItemNotFound() {
        when(itemRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ItemNotFoundException.class, () -> {
            itemService.updateItem(new ItemDto(), 1L, 1L);
        });
    }

    @Test
    public void testUpdateItemUnauthorized() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.isItemBelongOwner(anyLong(), anyLong())).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () -> {
            itemService.updateItem(new ItemDto(), 1L, 1L);
        });
    }

    @Test
    public void testGetItemByIdSuccessAsOwner() {
        Long itemId = 1L;
        Long userId = 1L;
        Item item = new Item();
        item.setId(itemId);
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);
        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setOwnerId(owner.getId());

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        lenient().when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(eq(itemId), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        lenient().when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(eq(itemId), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(commentRepository.findByItemId(itemId)).thenReturn(new ArrayList<>());
        lenient().when(commentMapper.toCommentDto(any())).thenReturn(new CommentDto());
        when(itemMapper.toItemDto(eq(item), any(), any(), any())).thenReturn(expectedItemDto);

        ItemDto result = itemService.getItemById(itemId, userId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals(userId, result.getOwnerId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        verify(commentRepository).findByItemId(itemId);
    }

    @Test
    public void testGetItemByIdNotFound() {
        Long itemId = 1L;
        Long userId = 1L;
        when(itemRepository.findById(itemId)).thenThrow(new ItemNotFoundException());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }

    @Test
    public void testGetAllItemsWithBookingDetails() {
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);
        List<Item> items = List.of(item1, item2);
        Page<Item> page = new PageImpl<>(items);

        when(itemRepository.findAllByOwnerId(ownerId, pageable)).thenReturn(page);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(anyLong(), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyLong(), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(itemMapper.toItemDto(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            ItemDto dto = new ItemDto();
            dto.setId(item.getId());
            return dto;
        });

        Page<ItemDto> resultPage = itemService.getAllItemsWithBookingDetails(ownerId, pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getContent().size());
        verify(bookingRepository, times(2)).findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(anyLong(), eq(Status.APPROVED), any(LocalDateTime.class));
        verify(bookingRepository, times(2)).findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(anyLong(), eq(Status.APPROVED), any(LocalDateTime.class));
        verify(itemMapper, times(2)).toItemDto(any(Item.class));
    }

    @Test
    public void testGetAllItemsWithBookingDetailsEmpty() {
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = Page.empty(pageable);

        when(itemRepository.findAllByOwnerId(ownerId, pageable)).thenReturn(page);

        Page<ItemDto> resultPage = itemService.getAllItemsWithBookingDetails(ownerId, pageable);
        assertTrue(resultPage.isEmpty());
    }

    @Test
    public void testIsItemBelongOwnerTrue() {
        long itemId = 1L;
        long ownerId = 1L;
        when(itemRepository.isItemBelongOwner(itemId, ownerId)).thenReturn(true);

        boolean result = itemService.isItemBelongOwner(itemId, ownerId);

        assertTrue(result);
    }

    @Test
    public void testIsItemBelongOwnerFalse() {
        long itemId = 1L;
        long ownerId = 1L;
        when(itemRepository.isItemBelongOwner(itemId, ownerId)).thenReturn(false);

        boolean result = itemService.isItemBelongOwner(itemId, ownerId);

        assertFalse(result);
    }

    @Test
    void addCommentSuccess() {
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        Item item = new Item();
        item.setId(itemId);
        User user = new User();
        user.setId(1L);
        user.setName("User User");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(commentDto.getAuthorId())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(eq(commentDto.getAuthorId()), eq(itemId), any(LocalDateTime.class)))
                .thenReturn(true);
        Comment comment = new Comment();
        when(commentMapper.toComment(eq(commentDto), eq(item), eq(user))).thenReturn(comment);
        CommentDto returnedCommentDto = new CommentDto();
        returnedCommentDto.setAuthorName(user.getName());
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(returnedCommentDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto result = itemService.addComment(itemId, commentDto);

        assertNotNull(result);
        assertEquals("User User", result.getAuthorName());
        verify(commentRepository).save(comment);
    }

    @Test
    void addCommentThrowsItemNotFoundException() {
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.addComment(itemId, commentDto));
    }

    @Test
    void addCommentThrowsUserNotFoundException() {
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        Item item = new Item();
        item.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(commentDto.getAuthorId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.addComment(itemId, commentDto));
    }

    @Test
    void addCommentThrowsInvalidCommentException() {
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        Item item = new Item();
        item.setId(itemId);
        User user = new User();
        user.setId(1L);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(commentDto.getAuthorId())).thenReturn(Optional.of(user));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(eq(commentDto.getAuthorId()), eq(itemId), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(InvalidCommentException.class, () -> itemService.addComment(itemId, commentDto));
    }
}
