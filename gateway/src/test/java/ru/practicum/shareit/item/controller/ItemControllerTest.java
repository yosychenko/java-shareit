package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ExceptionControllerAdvice;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mvc;

    private ItemDto itemDto;

    private ItemDto itemDtoWithNulls;

    private CommentDto commentDto;

    private Item item;

    private Comment comment;

    @BeforeEach
    void beforeEach() {
        BookingTimeIntervalDto bookingTimeIntervalDtoLast = BookingTimeIntervalDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .bookerId(1L)
                .build();
        BookingTimeIntervalDto bookingTimeIntervalDtoNext = BookingTimeIntervalDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 5, 13, 10, 0))
                .end(LocalDateTime.of(2023, 5, 20, 10, 0))
                .bookerId(1L)
                .build();

        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john.doe@mail.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.of(2023, 4, 13, 10, 0));

        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("John")
                .created(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(1L)
                .build();

        itemDtoWithNulls = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .requestId(1L)
                .build();

        item = new Item();
        item.setId(1L);
        item.setName("item_name");
        item.setDescription("item_description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("comment");
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2023, 4, 20, 10, 0));

    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(item);

        mvc.perform(post("/items")
                        .content(TestUtils.asJsonString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(TestUtils.asJsonString(commentDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created[0]", is(commentDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(commentDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(commentDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(commentDto.getCreated().getHour())))
                .andExpect(jsonPath("$.created[4]", is(commentDto.getCreated().getMinute())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(item);

        mvc.perform(patch("/items/1")
                        .content(TestUtils.asJsonString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testGetItemById() throws Exception {
        when(itemService.getItemByIdWithBookingIntervals(anyLong(), anyLong())).thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .content(TestUtils.asJsonString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testGetItemByIdNonExistentItem() throws Exception {
        when(itemService.getItemByIdWithBookingIntervals(anyLong(), anyLong())).thenThrow(new ItemNotFoundException(1L));

        mvc.perform(get("/items/1")
                        .content(TestUtils.asJsonString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Вещь c ID=1 не найдена.")));
    }

    @Test
    void testGetUserItems() throws Exception {
        when(itemService.getUserItemsWithBookingIntervals(anyLong(), any())).thenReturn(List.of(itemDtoWithNulls));

        mvc.perform(get("/items?from=1&size=20")
                        .content(TestUtils.asJsonString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithNulls.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithNulls.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithNulls.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithNulls.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoWithNulls.getRequestId()), Long.class));
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemService.searchItems(anyString(), any())).thenReturn(List.of(item));

        mvc.perform(get("/items/search?text=search&from=1&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithNulls.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithNulls.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithNulls.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithNulls.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoWithNulls.getRequestId()), Long.class));
    }

    @Test
    void testSearchItemsEmptyTextParam() throws Exception {
        mvc.perform(get("/items/search?&from=1&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
