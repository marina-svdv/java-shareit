package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(concat('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(concat('%', :text, '%'))) " +
            "AND i.available = true")
    Page<Item> findBySubstring(@Param("text") String text, Pageable pageable);

    @Query("SELECT COUNT(i) > 0 FROM Item i WHERE i.id = :itemId AND i.owner.id = :ownerId")
    boolean isItemBelongOwner(@Param("itemId") Long itemId, @Param("ownerId") Long ownerId);
}
