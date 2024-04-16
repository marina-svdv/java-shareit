package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId, Sort sort);
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(concat('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(concat('%', ?1, '%'))")
    List<Item> findBySubstring(String text);

    @Query("SELECT COUNT(i) > 0 FROM Item i WHERE i.id = :itemId AND i.owner.id = :ownerId")
    boolean isItemBelongOwner(@Param("itemId") Long itemId, @Param("ownerId") Long ownerId);
}
