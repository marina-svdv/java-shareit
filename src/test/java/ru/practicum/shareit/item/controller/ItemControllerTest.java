package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemMapper itemMapper;

    @Test
    void createItemShouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful tool");
        itemDto.setAvailable(true);
        Item savedItem = new Item();
        savedItem.setId(1L);
        when(itemService.createItem(any(ItemDto.class), eq(1L))).thenReturn(savedItem);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\",\"description\":\"Powerful tool\",\"available\":true}")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful tool"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createItemShouldHandleValidationError() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"\",\"available\":null}")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItemShouldReturnUpdatedItem() throws Exception {
        long itemId = 1L;
        ItemDto itemToUpdate = new ItemDto();
        itemToUpdate.setName("Drill Updated");
        itemToUpdate.setDescription("Even more powerful");
        itemToUpdate.setAvailable(true);
        when(itemService.getItemById(itemId, 1L)).thenReturn(itemToUpdate);
        when(itemMapper.toItemDto(any(ItemPatchDto.class), any(ItemDto.class))).thenReturn(itemToUpdate);
        when(itemService.updateItem(any(ItemDto.class), eq(itemId), eq(1L))).thenReturn(itemToUpdate);

        MockHttpServletRequestBuilder builder = patch("/items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Drill Updated\",\"description\":\"Even more powerful\",\"available\":true}")
                .header("X-Sharer-User-Id", "1");

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drill Updated"))
                .andExpect(jsonPath("$.description").value("Even more powerful"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItemShouldHandleInvalidData() throws Exception {
        long itemId = 1L;
        when(itemService.getItemById(itemId, 1L)).thenThrow(new IllegalArgumentException("Invalid item data"));

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"description\":\"Too short\",\"available\":null}")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemByIdShouldReturnItemDetails() throws Exception {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful tool");
        itemDto.setAvailable(true);
        when(itemService.getItemById(itemId, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful tool"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemByIdShouldReturnNotFoundWhenItemDoesNotExist() throws Exception {
        long invalidItemId = 999L;
        when(itemService.getItemById(invalidItemId, 1L)).thenThrow(new ItemNotFoundException());

        mockMvc.perform(get("/items/" + invalidItemId)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ItemNotFoundException));
    }

    @Test
    void getAllItemsByOwnerShouldReturnItemsList() throws Exception {
        int page = 0;
        int size = 10;
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Drill");
        item1.setDescription("Powerful tool");
        item1.setAvailable(true);
        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Saw");
        item2.setDescription("Sharp tool");
        item2.setAvailable(true);
        List<ItemDto> items = Arrays.asList(item1, item2);
        Page<ItemDto> pageResult = new PageImpl<>(items);

        when(itemService.getAllItemsWithBookingDetails(1L, PageRequest.of(page, size, Sort.by("name").descending())))
                .thenReturn(pageResult);

        mockMvc.perform(get("/items")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].name").value("Saw"));
    }

    @Test
    void getAllItemsByOwnerShouldReturnEmptyList() throws Exception {
        int page = 0;
        int size = 10;
        Page<ItemDto> pageResult = new PageImpl<>(List.of());

        when(itemService.getAllItemsWithBookingDetails(1L, PageRequest.of(page, size, Sort.by("name").descending())))
                .thenReturn(pageResult);

        mockMvc.perform(get("/items")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void getItemsBySubstringShouldReturnItemsWhenValidSubstringProvided() throws Exception {
        List<ItemDto> items = List.of(new ItemDto());
        Page<ItemDto> page = new PageImpl<>(items);
        when(itemService.getItemsBySubstring("test", PageRequest.of(0, 10))).thenReturn(page);

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getItemsBySubstringShouldReturnEmptyListWhenNoItemsFound() throws Exception {
        Page<ItemDto> page = new PageImpl<>(List.of());
        when(itemService.getItemsBySubstring("empty", PageRequest.of(0, 10))).thenReturn(page);

        mockMvc.perform(get("/items/search")
                        .param("text", "empty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void addCommentShouldCreateCommentWhenValidRequest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Nice item!");
        commentDto.setAuthorId(1L);

        CommentDto returnedComment = new CommentDto();
        returnedComment.setId(1L);
        returnedComment.setText("Nice item!");
        returnedComment.setAuthorName("User User");

        when(itemService.addComment(1L, commentDto)).thenReturn(returnedComment);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Nice item!")))
                .andExpect(jsonPath("$.authorName", is("User User")));
    }

    @Test
    void addCommentShouldReturnBadRequestWhenCommentTextIsBlank() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }
}
