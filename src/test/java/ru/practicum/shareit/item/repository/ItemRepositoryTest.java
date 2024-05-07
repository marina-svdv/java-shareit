package ru.practicum.shareit.item.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveItemShouldPersistItem() {
        User author = new User();
        author.setName("Test User");
        author.setEmail("test@example.com");
        User savedAuthor = userRepository.save(author);

        Item item = new Item();
        item.setName("Test Item");
        item.setAvailable(true);
        item.setDescription("Test description");
        item.setOwner(savedAuthor);

        Item savedItem = itemRepository.save(item);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Test Item");
    }

    @Test
    public void updateItemShouldUpdateExistingItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setAvailable(true);
        item.setDescription("Test Description");

        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");
        User savedOwner = userRepository.save(owner);
        item.setOwner(savedOwner);

        Item savedItem = itemRepository.save(item);

        savedItem.setName("Updated Item");
        savedItem.setDescription("Updated Description");

        Item updatedItem = itemRepository.save(savedItem);

        Item retrievedItem = itemRepository.findById(updatedItem.getId()).orElse(null);

        assertThat(retrievedItem).isNotNull();
        assertThat(retrievedItem.getName()).isEqualTo("Updated Item");
        assertThat(retrievedItem.getAvailable()).isTrue();
        assertThat(retrievedItem.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void findItemByIdShouldReturnItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setAvailable(true);
        item.setDescription("Test Description");

        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");
        User savedOwner = userRepository.save(owner);
        item.setOwner(savedOwner);

        Item savedItem = itemRepository.save(item);

        Item foundItem = itemRepository.findById(savedItem.getId()).orElse(null);

        assertThat(foundItem).isNotNull();
        assertThat(foundItem).isEqualTo(savedItem);
    }

    @Test
    public void deleteItemByIdShouldRemoveItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setAvailable(true);
        item.setDescription("Test Description");

        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");
        User savedOwner = userRepository.save(owner);
        item.setOwner(savedOwner);

        Item savedItem = entityManager.persistFlushFind(item);

        itemRepository.deleteById(savedItem.getId());

        Item deletedItem = entityManager.find(Item.class, savedItem.getId());

        assertThat(deletedItem).isNull();
    }

    @Test
    public void findAllItemsShouldReturnAllItems() {
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setAvailable(true);
        item1.setDescription("Description 1");

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setAvailable(true);
        item2.setDescription("Description 2");

        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");
        User savedOwner = userRepository.save(owner);

        item1.setOwner(savedOwner);
        item2.setOwner(savedOwner);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        List<Item> items = itemRepository.findAll();

        assertThat(items).isNotNull();
        assertThat(items.size()).isEqualTo(2);
    }

    @Test
    public void findAllByOwnerIdShouldReturnItems() {
        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");
        User savedOwner = userRepository.save(owner);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setAvailable(true);
        item1.setDescription("Description 1");
        item1.setOwner(savedOwner);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setAvailable(true);
        item2.setDescription("Description 2");
        item2.setOwner(savedOwner);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> items = itemRepository.findAllByOwnerId(savedOwner.getId(), pageable);

        assertThat(items).isNotNull();
        assertThat(items.getContent().size()).isEqualTo(2);
        assertThat(items.getContent()).containsExactly(item1, item2);
    }

    @Test
    public void findBySubstringShouldReturnMatchingItems() {
        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");
        User savedOwner = userRepository.save(owner);

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setAvailable(true);
        item1.setDescription("Description 1");
        item1.setOwner(savedOwner);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setAvailable(true);
        item2.setDescription("Description 2");
        item2.setOwner(savedOwner);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> items = itemRepository.findBySubstring("1", pageable);

        assertThat(items).isNotNull();
        assertThat(items.getContent().size()).isEqualTo(1);
        assertThat(items.getContent().get(0)).isEqualTo(item1);
    }

    @Test
    public void isItemBelongOwnerShouldReturnTrueWhenOwnerMatches() {
        User owner = new User();
        owner.setName("Test Owner");
        owner.setEmail("owner@example.com");
        User savedOwner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Test Item");
        item.setAvailable(true);
        item.setDescription("Test Description");
        item.setOwner(savedOwner);
        Item savedItem = itemRepository.save(item);

        boolean isBelongOwner = itemRepository.isItemBelongOwner(savedItem.getId(), savedOwner.getId());

        assertThat(isBelongOwner).isTrue();
    }

    @Test
    public void isItemBelongOwnerShouldReturnFalseWhenOwnerDoesNotMatch() {
        User owner1 = new User();
        owner1.setName("Test Owner 1");
        owner1.setEmail("owner1@example.com");
        User savedOwner1 = userRepository.save(owner1);

        User owner2 = new User();
        owner2.setName("Test Owner 2");
        owner2.setEmail("owner2@example.com");
        User savedOwner2 = userRepository.save(owner2);

        Item item = new Item();
        item.setName("Test Item");
        item.setAvailable(true);
        item.setDescription("Test Description");
        item.setOwner(savedOwner1);
        Item savedItem = itemRepository.save(item);

        boolean isBelongOwner = itemRepository.isItemBelongOwner(savedItem.getId(), savedOwner2.getId());

        assertThat(isBelongOwner).isFalse();
    }
}
