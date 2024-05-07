package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void testToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("This is a test item");
        item.setAvailable(true);

        User owner = new User();
        owner.setId(1L);
        item.setOwner(owner);

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(owner.getId(), itemDto.getOwnerId());
    }

    @Test
    public void testToItemDtoWithNullItem() {
        assertNull(itemMapper.toItemDto(null));
    }

    @Test
    public void testToItemDtoWithBooking() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("This is a test item");
        item.setAvailable(true);
        User owner = new User(1L, "User User", "user.user@example.com");
        item.setOwner(owner);

        BookingDto lastBooking = new BookingDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);

        BookingDto nextBooking = new BookingDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(3L);

        List<CommentDto> comments = new ArrayList<>();
        comments.add(new CommentDto());

        ItemDto itemDto = itemMapper.toItemDto(item, lastBooking, nextBooking, comments);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(lastBooking, itemDto.getLastBooking());
        assertEquals(nextBooking, itemDto.getNextBooking());
        assertEquals(comments, itemDto.getComments());
    }

    @Test
    public void testToItemDtoWithNullBooking() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("This is a test item");
        item.setAvailable(true);
        User owner = new User(1L, "User User", "user.user@example.com");
        item.setOwner(owner);

        ItemDto itemDto = itemMapper.toItemDto(item, null, null, null);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertEquals(new ArrayList<>(), itemDto.getComments());
    }

    @Test
    public void testToItemDtoWithPatchDto() {
        ItemDto itemToUpdate = new ItemDto();
        itemToUpdate.setId(1L);
        itemToUpdate.setName("Old Name");
        itemToUpdate.setDescription("Old Description");
        itemToUpdate.setAvailable(false);

        ItemPatchDto patchDto = new ItemPatchDto();
        patchDto.setName("New Name");
        patchDto.setDescription("New Description");
        patchDto.setAvailable(true);

        ItemDto updatedItemDto = itemMapper.toItemDto(patchDto, itemToUpdate);

        assertEquals(itemToUpdate.getId(), updatedItemDto.getId());
        assertEquals(patchDto.getName(), updatedItemDto.getName());
        assertEquals(patchDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(patchDto.getAvailable(), updatedItemDto.getAvailable());
    }

    @Test
    public void testToItemDtoWithNullPatchDto() {
        ItemDto itemToUpdate = new ItemDto();
        itemToUpdate.setId(1L);
        itemToUpdate.setName("Old Name");
        itemToUpdate.setDescription("Old Description");
        itemToUpdate.setAvailable(false);

        ItemDto updatedItemDto = itemMapper.toItemDto(null, itemToUpdate);

        assertEquals(itemToUpdate.getId(), updatedItemDto.getId());
        assertEquals(itemToUpdate.getName(), updatedItemDto.getName());
        assertEquals(itemToUpdate.getDescription(), updatedItemDto.getDescription());
        assertEquals(itemToUpdate.getAvailable(), updatedItemDto.getAvailable());
    }

    @Test
    public void testToItemWithDtoAndOwner() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Test Item");
        dto.setDescription("This is a test item");
        dto.setAvailable(true);

        User owner = new User();
        owner.setId(1L);

        Item item = itemMapper.toItem(dto, owner);

        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
    }

    @Test
    public void testToItemWithNullDto() {
        User owner = new User();
        owner.setId(1L);

        assertNull(itemMapper.toItem(null, owner));
    }

    @Test
    public void testToItemWithNullOwner() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Test Item");
        dto.setDescription("This is a test item");
        dto.setAvailable(true);

        assertNull(itemMapper.toItem(dto, null));
    }
}
