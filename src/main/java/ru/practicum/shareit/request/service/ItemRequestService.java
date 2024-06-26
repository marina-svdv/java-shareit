package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getItemRequestsByUser(Long userId);

    Page<ItemRequestDto> getAllItemRequests(Long userId, Pageable pageable);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
