package ru.practicum.shareit.booking.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void saveBookingShouldPersistBooking() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);

        Booking booking = new Booking(null, item, user, user, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking savedBooking = bookingRepository.save(booking);

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getStatus()).isEqualTo(Status.WAITING);
        assertThat(savedBooking.getItem()).isEqualTo(item);
    }

    @Test
    public void updateBookingShouldUpdateExistingBooking() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);

        Booking booking = new Booking(null, item, user, user, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking savedBooking = entityManager.persistFlushFind(booking);

        savedBooking.setStatus(Status.APPROVED);
        bookingRepository.save(savedBooking);

        Booking updatedBooking = bookingRepository.findById(savedBooking.getId()).orElse(null);
        assertThat(updatedBooking).isNotNull();
        assertThat(updatedBooking.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    public void findBookingByIdShouldReturnBookingWhenExists() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);

        Booking booking = new Booking(null, item, user, user, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking savedBooking = entityManager.persistFlushFind(booking);

        Optional<Booking> foundBooking = bookingRepository.findById(savedBooking.getId());
        assertThat(foundBooking).isNotEmpty();
        assertThat(foundBooking.get()).isEqualTo(savedBooking);
    }

    @Test
    public void findBookingByIdShouldReturnEmptyWhenDoesNotExist() {
        Optional<Booking> foundBooking = bookingRepository.findById(11L);
        assertThat(foundBooking).isEmpty();
    }

    @Test
    public void deleteBookingShouldRemoveBooking() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);
        Booking booking = new Booking(null, item, user, user, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking savedBooking = entityManager.persistFlushFind(booking);

        assertThat(savedBooking).isNotNull();

        bookingRepository.delete(savedBooking);
        Booking deletedBooking = bookingRepository.findById(savedBooking.getId()).orElse(null);
        assertThat(deletedBooking).isNull();
    }

    @Test
    public void findAllByBookerIdShouldReturnCorrectBookings() {
        User booker = entityManager.persist(new User(null, "Booker Name", "booker@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, booker, null));
        Booking booking1 = new Booking(null, item, booker, booker, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking booking2 = new Booking(null, item, booker, booker, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4), Status.APPROVED);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("start").descending());
        Page<Booking> bookings = bookingRepository.findAllByBookerId(booker.getId(), pageRequest);

        assertThat(bookings.getContent()).hasSize(2);
        assertThat(bookings.getContent()).containsExactlyInAnyOrder(booking1, booking2);
    }

    @Test
    public void findAllByBookerIdAndStatusShouldReturnCorrectBookings() {
        User booker = entityManager.persist(new User(null, "Booker Name", "booker@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description", true,
                booker, null));
        Booking booking1 = new Booking(null, item, booker, booker, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking booking2 = new Booking(null, item, booker, booker, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4), Status.WAITING);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("start").descending());
        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.WAITING, pageRequest);

        assertThat(bookings.getContent()).hasSize(2);
        assertThat(bookings.getContent()).containsExactlyInAnyOrder(booking1, booking2);
    }

    @Test
    public void findAllByBookerIdAndEndBeforeShouldReturnCorrectBookings() {
        User booker = entityManager.persist(new User(null, "Booker Name", "booker@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, booker, null));
        LocalDateTime now = LocalDateTime.now();
        Booking bookingBefore = new Booking(null, item, booker, booker, now.minusDays(2),
                now.minusDays(1), Status.APPROVED);
        Booking bookingAfter = new Booking(null, item, booker, booker, now.minusDays(1),
                now.plusDays(1), Status.APPROVED);

        entityManager.persist(bookingBefore);
        entityManager.persist(bookingAfter);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("end").ascending());
        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), now, pageRequest);

        assertThat(bookings.getContent()).containsExactly(bookingBefore);
        assertThat(bookings.getContent()).doesNotContain(bookingAfter);
    }

    @Test
    public void findAllByBookerIdAndStartAfterShouldReturnCorrectBookings() {
        User booker = entityManager.persist(new User(null, "Booker Name", "booker@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, booker, null));
        LocalDateTime now = LocalDateTime.now();
        Booking bookingBefore = new Booking(null, item, booker, booker, now.minusDays(2),
                now.minusDays(1), Status.APPROVED);
        Booking bookingAfter = new Booking(null, item, booker, booker, now.plusDays(1),
                now.plusDays(2), Status.APPROVED);

        entityManager.persist(bookingBefore);
        entityManager.persist(bookingAfter);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("start").ascending());
        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), now, pageRequest);

        assertThat(bookings.getContent()).containsExactly(bookingAfter);
        assertThat(bookings.getContent()).doesNotContain(bookingBefore);
    }

    @Test
    public void findAllByBookerIdAndStartBeforeAndEndAfterShouldReturnCorrectBookings() {
        User booker = entityManager.persist(new User(null, "Booker Name", "booker@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description", true,
                booker, null));
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking validBooking = new Booking(null, item, booker, booker, start.minusHours(1),
                end.plusHours(1), Status.APPROVED);
        Booking outOfRangeBooking = new Booking(null, item, booker, booker, end.plusHours(2),
                end.plusHours(3), Status.APPROVED);

        entityManager.persist(validBooking);
        entityManager.persist(outOfRangeBooking);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(booker.getId(),
                end, start, pageRequest);

        assertThat(bookings.getContent()).containsExactly(validBooking);
        assertThat(bookings.getContent()).doesNotContain(outOfRangeBooking);
    }

    @Test
    public void findAllByOwnerIdShouldReturnCorrectBookings() {
        User owner = entityManager.persist(new User(null, "Owner Name", "owner@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, owner, null));
        Booking booking1 = new Booking(null, item, owner, owner, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), Status.APPROVED);
        Booking booking2 = new Booking(null, item, owner, owner, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.APPROVED);

        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("start").ascending());
        Page<Booking> bookings = bookingRepository.findAllByOwnerId(owner.getId(), pageRequest);

        assertThat(bookings.getContent()).hasSize(2);
        assertThat(bookings.getContent()).containsExactlyInAnyOrder(booking1, booking2);
    }

    @Test
    public void findAllByOwnerIdAndStatusShouldReturnCorrectBookings() {
        User owner = entityManager.persist(new User(null, "Owner Name", "owner@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, owner, null));
        Booking booking1 = new Booking(null, item, owner, owner, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), Status.APPROVED);
        Booking booking2 = new Booking(null, item, owner, owner, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), Status.REJECTED);

        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("start").ascending());
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(), Status.APPROVED, pageRequest);

        assertThat(bookings.getContent()).containsExactly(booking1);
        assertThat(bookings.getContent()).doesNotContain(booking2);
    }

    @Test
    public void findAllByOwnerIdAndEndBeforeShouldReturnCorrectBookings() {
        User owner = entityManager.persist(new User(null, "Owner Name", "owner@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, owner, null));
        LocalDateTime now = LocalDateTime.now();
        Booking bookingBefore = new Booking(null, item, owner, owner, now.minusDays(2),
                now.minusHours(1), Status.APPROVED);
        Booking bookingAfter = new Booking(null, item, owner, owner, now.minusDays(1),
                now.plusHours(1), Status.APPROVED);

        entityManager.persist(bookingBefore);
        entityManager.persist(bookingAfter);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("end").ascending());
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndEndBefore(owner.getId(), now, pageRequest);

        assertThat(bookings.getContent()).containsExactly(bookingBefore);
        assertThat(bookings.getContent()).doesNotContain(bookingAfter);
    }

    @Test
    public void findAllByOwnerIdAndStartAfterShouldReturnCorrectBookings() {
        User owner = entityManager.persist(new User(null, "Owner Name", "owner@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, owner, null));

        LocalDateTime now = LocalDateTime.now();
        Booking bookingBefore = new Booking(null, item, owner, owner, now.minusHours(2),
                now.minusHours(1), Status.APPROVED);
        Booking bookingAfter = new Booking(null, item, owner, owner, now.plusHours(1),
                now.plusDays(1), Status.APPROVED);

        entityManager.persist(bookingBefore);
        entityManager.persist(bookingAfter);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("start").ascending());
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndStartAfter(owner.getId(), now, pageRequest);

        assertThat(bookings.getContent()).containsExactly(bookingAfter);
        assertThat(bookings.getContent()).doesNotContain(bookingBefore);
    }

    @Test
    public void findAllByOwnerIdAndStartBeforeAndEndAfterShouldReturnCorrectBookings() {
        User owner = entityManager.persist(new User(null, "Owner Name", "owner@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, owner, null));
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking validBooking = new Booking(null, item, owner, owner, start.minusHours(2),
                end.plusHours(2), Status.APPROVED);
        Booking outOfRangeBefore = new Booking(null, item, owner, owner, start.minusDays(2),
                start.minusHours(1), Status.APPROVED);
        Booking outOfRangeAfter = new Booking(null, item, owner, owner, end.plusHours(1),
                end.plusDays(1), Status.APPROVED);

        entityManager.persist(validBooking);
        entityManager.persist(outOfRangeBefore);
        entityManager.persist(outOfRangeAfter);
        entityManager.flush();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("start").ascending());
        Page<Booking> bookings = bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(owner.getId(),
                start, end, pageRequest);

        assertThat(bookings.getContent()).containsExactly(validBooking);
        assertThat(bookings.getContent()).doesNotContain(outOfRangeBefore, outOfRangeAfter);
    }

    @Test
    public void findByItemIdAndEndAfterAndStartBeforeShouldReturnCorrectBookings() {
        User user = entityManager.persist(new User(null, "User One", "user1@example.com"));
        Item item = entityManager.persist(new Item(null, "Item One", "Description",
                true, user, null));
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Booking validBooking = new Booking(null, item, user, user, start.minusHours(1),
                end.plusHours(1), Status.APPROVED);
        Booking outOfRangeBooking = new Booking(null, item, user, user, end.plusHours(2),
                end.plusHours(3), Status.APPROVED);

        entityManager.persist(validBooking);
        entityManager.persist(outOfRangeBooking);
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findByItemIdAndEndAfterAndStartBefore(item.getId(), end, start);

        assertThat(bookings).containsExactly(validBooking);
        assertThat(bookings).doesNotContain(outOfRangeBooking);
    }

    @Test
    public void findFirstByItemIdAndStatusAndStartBeforeOrderByEndDescShouldReturnBooking() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);
        Booking booking1 = new Booking(null, item, user, user, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2), Status.APPROVED);
        Booking booking2 = new Booking(null, item, user, user, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), Status.APPROVED);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        Optional<Booking> foundBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                item.getId(), Status.APPROVED, LocalDateTime.now());

        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get()).isEqualTo(booking1);
    }

    @Test
    public void findFirstByItemIdAndStatusAndStartAfterOrderByStartAscShouldReturnBooking() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);
        Booking booking1 = new Booking(null, item, user, user, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3), Status.APPROVED);
        Booking booking2 = new Booking(null, item, user, user, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4), Status.APPROVED);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        Optional<Booking> foundBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                item.getId(), Status.APPROVED, LocalDateTime.now());

        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get()).isEqualTo(booking1);
    }

    @Test
    public void existsByBookerIdAndItemIdAndEndBeforeShouldReturnTrueIfBookingExists() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);

        LocalDateTime end = LocalDateTime.now().plusHours(1);
        Booking booking = new Booking(null, item, user, user, LocalDateTime.now().minusHours(2),
                end.minusMinutes(10), Status.APPROVED);
        entityManager.persist(booking);
        entityManager.flush();
        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), end);

        assertThat(exists).isTrue();
    }

    @Test
    public void existsByBookerIdAndItemIdAndEndBeforeShouldReturnFalseIfNoBookingExists() {
        User user = new User(null, "Test Booker", "booker@example.com");
        Item item = new Item(null, "Test Item", "Description", true, user, null);
        entityManager.persist(user);
        entityManager.persist(item);

        LocalDateTime end = LocalDateTime.now().plusHours(1);
        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(user.getId(), item.getId(), end);

        assertThat(exists).isFalse();
    }
}
