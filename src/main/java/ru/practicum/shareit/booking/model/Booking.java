package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;

    @Column(nullable = false)
    LocalDateTime start;

    @Column(name = "\"end\"", nullable = false)
    LocalDateTime end;

    @Enumerated(EnumType.STRING)
    Status status;
}
