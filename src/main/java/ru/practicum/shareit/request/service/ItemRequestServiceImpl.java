package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        return null;
    }

    @Override
    public List<ItemRequest> getAllItemRequests(Long userId) {
        return null;
    }
}
