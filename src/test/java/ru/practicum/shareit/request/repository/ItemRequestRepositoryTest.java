package ru.practicum.shareit.request.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    @Test
    public void saveItemRequestShouldPersistItemRequest() {
        User user = new User(null, "Requester User", "requester@example.com");
        entityManager.persist(user);

        ItemRequest itemRequest = new ItemRequest(null, "Need a new laptop", user,
                LocalDateTime.now(), null);

        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        assertThat(savedItemRequest).isNotNull();
        assertThat(savedItemRequest.getId()).isNotNull();
        assertThat(savedItemRequest.getDescription()).isEqualTo("Need a new laptop");
        assertThat(savedItemRequest.getRequester()).isEqualTo(user);
    }

    @Test
    public void updateItemRequestShouldUpdateExistingRequest() {
        User user = new User(null, "Requester User", "requester@example.com");
        entityManager.persist(user);
        ItemRequest itemRequest = new ItemRequest(null, "Need a new laptop", user,
                LocalDateTime.now(), null);
        ItemRequest savedItemRequest = entityManager.persistFlushFind(itemRequest);
        savedItemRequest.setDescription("Need a new laptop");

        itemRequestRepository.save(savedItemRequest);

        entityManager.flush();
        entityManager.clear();

        ItemRequest updatedItemRequest = itemRequestRepository.findById(savedItemRequest.getId()).orElse(null);

        assertThat(updatedItemRequest).isNotNull();
        assertThat(updatedItemRequest.getDescription()).isEqualTo("Need a new laptop");
    }

    @Test
    public void findAllByRequesterIdShouldReturnItemRequests() {
        User user = new User(null, "Requester User", "requester@example.com");
        entityManager.persist(user);

        ItemRequest itemRequest1 = new ItemRequest(null, "Need a new camera", user,
                LocalDateTime.now(), null);
        ItemRequest itemRequest2 = new ItemRequest(null, "Looking for a microphone", user,
                LocalDateTime.now(), null);
        entityManager.persist(itemRequest1);
        entityManager.persist(itemRequest2);

        Collection<ItemRequest> foundItemRequests = itemRequestRepository.findAllByRequesterId(user.getId());

        assertThat(foundItemRequests).hasSize(2);
        assertThat(foundItemRequests).extracting("description").containsExactlyInAnyOrder(
                "Need a new camera", "Looking for a microphone");
    }

    @Test
    public void deleteItemRequestShouldRemoveItemRequest() {
        User user = new User(null, "Requester User", "requester@example.com");
        entityManager.persist(user);

        ItemRequest itemRequest = new ItemRequest(null, "Need a bike", user, LocalDateTime.now(), null);
        ItemRequest savedItemRequest = entityManager.persistFlushFind(itemRequest);

        assertThat(itemRequestRepository.findById(savedItemRequest.getId())).isPresent();

        itemRequestRepository.deleteById(savedItemRequest.getId());
        entityManager.flush();

        assertThat(itemRequestRepository.findById(savedItemRequest.getId())).isEmpty();
    }

    @Test
    public void findAllByRequesterIdShouldReturnRequests() {
        User user = new User(null, "Requester User", "requester@example.com");
        entityManager.persist(user);
        User otherUser = new User(null, "Other User", "other@example.com");
        entityManager.persist(otherUser);

        ItemRequest request1 = new ItemRequest(null, "Need a bike", user, LocalDateTime.now(), null);
        ItemRequest request2 = new ItemRequest(null, "Need a helmet", user, LocalDateTime.now(), null);
        entityManager.persist(request1);
        entityManager.persist(request2);
        entityManager.persist(new ItemRequest(null, "Need a camera", otherUser,
                LocalDateTime.now(), null));

        entityManager.flush();

        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(ItemRequest::getDescription).containsExactlyInAnyOrder(
                "Need a bike", "Need a helmet");
    }

    @Test
    public void findAllExcludeUserShouldReturnRequestsExcludingSpecificUser() {
        User user = new User(null, "Requester User", "requester@example.com");
        entityManager.persist(user);
        User otherUser = new User(null, "Other User", "other@example.com");
        entityManager.persist(otherUser);

        entityManager.persist(new ItemRequest(null, "Need a bike", user, LocalDateTime.now(), null));
        ItemRequest otherRequest1 = new ItemRequest(null, "Need a helmet", otherUser,
                LocalDateTime.now(), null);
        ItemRequest otherRequest2 = new ItemRequest(null, "Need a camera", otherUser,
                LocalDateTime.now(), null);
        entityManager.persist(otherRequest1);
        entityManager.persist(otherRequest2);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> page = itemRequestRepository.findAllExcludeUser(user.getId(), pageable);

        assertThat(page).hasSize(2);
        assertThat(page.getContent()).containsExactlyInAnyOrder(otherRequest1, otherRequest2);
    }
}
