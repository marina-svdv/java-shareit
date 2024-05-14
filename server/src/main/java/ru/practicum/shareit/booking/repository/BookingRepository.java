package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerId(Long bookerI, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                             Pageable pageable);

    Page<Booking> findAllByOwnerId(Long userId, Pageable pageable);

    Page<Booking> findAllByOwnerIdAndStatus(Long ownerId, Status status, Pageable pageable);

    Page<Booking> findAllByOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end,
                                                            Pageable pageable);

    List<Booking> findByItemIdAndEndAfterAndStartBefore(Long itemId, LocalDateTime start, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, Status status, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime start);

    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime beforeTime);
}
