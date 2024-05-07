package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void addItemRequestShouldCreateRequest() {
        User user = new User(1L, "User One", "user.one@example.com");
        ItemRequestDto itemRequestDto = new ItemRequestDto(null,
                "Need a camera", 1L, null, null);
        ItemRequest itemRequest = new ItemRequest(1L, "Need a camera", user, LocalDateTime.now(), null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(itemRequestDto)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.addItemRequest(itemRequestDto, 1L);

        assertNotNull(result);
        assertEquals("Need a camera", result.getDescription());
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void addItemRequestShouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.addItemRequest(new ItemRequestDto(), 1L));
        verify(userRepository).findById(1L);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getItemRequestsByUserShouldReturnListOfRequests() {
        Long userId = 1L;
        List<ItemRequest> requests = List.of(new ItemRequest(1L, "Need a camera",
                new User(userId, "User", "email"), LocalDateTime.now(), null));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "User", "email")));
        when(itemRequestRepository.findAllByRequesterId(userId)).thenReturn(requests);
        when(itemRequestMapper.toItemRequestDto(any())).thenAnswer(invocation -> {
            ItemRequest ir = invocation.getArgument(0);
            return new ItemRequestDto(ir.getId(), ir.getDescription(), ir.getRequester().getId(), ir.getCreated(), null);
        });

        List<ItemRequestDto> result = itemRequestService.getItemRequestsByUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Need a camera", result.get(0).getDescription());
        verify(userRepository).findById(userId);
        verify(itemRequestRepository).findAllByRequesterId(userId);
    }

    @Test
    void getItemRequestsByUserShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getItemRequestsByUser(userId));
        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findAllByRequesterId(anyLong());
    }

    @Test
    void getAllItemRequestsShouldReturnPagedRequests() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 2, Sort.by("created").descending());
        List<ItemRequest> itemRequests = List.of(
                new ItemRequest(2L, "Need a laptop",
                        new User(2L, "User Two", "email.two@example.com"), LocalDateTime.now(), null),
                new ItemRequest(3L, "Looking for a GoPro",
                        new User(3L, "User Three", "email.three@example.com"), LocalDateTime.now(), null)
        );
        Page<ItemRequest> page = new PageImpl<>(itemRequests, pageable, itemRequests.size());

        when(itemRequestRepository.findAllExcludeUser(eq(userId), any(Pageable.class))).thenReturn(page);
        when(itemRequestMapper.toItemRequestDto(any())).thenAnswer(invocation -> {
            ItemRequest ir = invocation.getArgument(0);
            return new ItemRequestDto(ir.getId(), ir.getDescription(), ir.getRequester().getId(), ir.getCreated(), null);
        });

        Page<ItemRequestDto> result = itemRequestService.getAllItemRequests(userId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Need a laptop", result.getContent().get(0).getDescription());
        verify(itemRequestRepository).findAllExcludeUser(userId, pageable);
    }

    @Test
    void getItemRequestByIdShouldReturnRequest() {
        Long userId = 1L, requestId = 1L;
        ItemRequest itemRequest = new ItemRequest(1L, "Need a camera",
                new User(userId, "User One", "email.one@example.com"), LocalDateTime.now(), null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId,
                "User One", "email.one@example.com")));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(new ItemRequestDto(1L,
                "Need a camera", userId, LocalDateTime.now(), null));

        ItemRequestDto result = itemRequestService.getItemRequestById(userId, requestId);

        assertNotNull(result);
        assertEquals("Need a camera", result.getDescription());
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void getItemRequestByIdShouldThrowExceptionWhenRequestNotFound() {
        Long userId = 1L, requestId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "User One",
                "email.one@example.com")));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequestById(userId, requestId));
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void getItemRequestByIdShouldThrowExceptionWhenUserNotFound() {
        Long userId = 99L, requestId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.getItemRequestById(userId, requestId));
        verify(userRepository).findById(userId);
        verify(itemRequestRepository, never()).findById(any());
    }
}
