package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequesterId(Long userId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id <> :userId")
    Page<ItemRequest> findAllExcludeUser(Long userId, Pageable pageable);
}
