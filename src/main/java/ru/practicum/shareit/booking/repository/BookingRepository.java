package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerI, Sort sort);
    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Sort sort);
    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);
    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);
    List<Booking> findAllByOwnerId(Long userId, Sort sort);
    List<Booking> findAllByOwnerIdAndStatus(Long ownerId, Status status, Sort sort);
    List<Booking> findAllByOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);
    List<Booking> findAllByOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);
    List<Booking> findByItemIdAndEndAfterAndStartBefore(Long itemId, LocalDateTime start, LocalDateTime end);
    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(Long itemId, Status status, LocalDateTime end);
    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime start);
    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime beforeTime);
}
