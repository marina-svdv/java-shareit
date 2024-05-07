package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    @Transactional
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return itemRequestRepository.findAllByRequesterId(userId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ItemRequestDto> getAllItemRequests(Long userId, Pageable pageable) {
        return itemRequestRepository.findAllExcludeUser(userId, pageable)
                .map(itemRequestMapper::toItemRequestDto);
    }

    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(ItemRequestNotFoundException::new);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }
}
