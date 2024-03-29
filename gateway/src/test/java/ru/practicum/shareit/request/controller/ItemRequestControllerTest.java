package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ExceptionControllerAdvice;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.item.dto.ItemDtoSimpleWithStatus;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestClient itemRequestClient;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;

    private ItemRequestResponseDto itemRequestResponseDto;

    private ItemDtoSimpleWithStatus itemDtoSimpleWithStatus;

    private UserDtoSimple userDtoSimple;

    private ResponseEntity<Object> itemRequestResponse;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();

        userDtoSimple = UserDtoSimple.builder()
                .id(1L)
                .name("John")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestor(userDtoSimple)
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .build();

        itemDtoSimpleWithStatus = ItemDtoSimpleWithStatus.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(1L)
                .build();

        itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .items(List.of(itemDtoSimpleWithStatus))
                .build();

        itemRequestResponse = new ResponseEntity<>(
                TestUtils.asJsonString(itemRequestDto),
                HttpStatus.OK
        );
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestClient.createItemRequest(anyLong(), any())).thenReturn(itemRequestResponse);

        mvc.perform(post("/requests")
                        .content(TestUtils.asJsonString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(userDtoSimple.getId()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(userDtoSimple.getName())))
                .andExpect(jsonPath("$.created[0]", is(itemRequestDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(itemRequestDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(itemRequestDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(itemRequestDto.getCreated().getHour())))
                .andExpect(jsonPath("$.created[4]", is(itemRequestDto.getCreated().getMinute())));

    }

    @Test
    void testCreateItemRequestMissingUserIdHeader() throws Exception {
        mvc.perform(post("/requests")
                        .content(TestUtils.asJsonString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateItemRequestUserNotFound() throws Exception {
        itemRequestResponse = new ResponseEntity<>(
                Map.of("message", "Пользователь c ID=1 не найден."),
                HttpStatus.NOT_FOUND
        );

        when(itemRequestClient.createItemRequest(anyLong(), any())).thenReturn(itemRequestResponse);

        mvc.perform(post("/requests")
                        .content(TestUtils.asJsonString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Пользователь c ID=1 не найден.")));

    }

    @Test
    void testGetAllItemRequestsFromUser() throws Exception {
        itemRequestResponse = new ResponseEntity<>(
                TestUtils.asJsonString(List.of(itemRequestResponseDto)),
                HttpStatus.OK
        );

        when(itemRequestClient.getAllItemRequestsFromUser(anyLong())).thenReturn(itemRequestResponse);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestResponseDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestResponseDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestResponseDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$[0].created[3]", is(itemRequestResponseDto.getCreated().getHour())))
                .andExpect(jsonPath("$[0].created[4]", is(itemRequestResponseDto.getCreated().getMinute())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDtoSimpleWithStatus.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemDtoSimpleWithStatus.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDtoSimpleWithStatus.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDtoSimpleWithStatus.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemDtoSimpleWithStatus.getRequestId()), Long.class));
    }

    @Test
    void testGetItemRequestsForUserOwnedItems() throws Exception {
        itemRequestResponse = new ResponseEntity<>(
                TestUtils.asJsonString(List.of(itemRequestResponseDto)),
                HttpStatus.OK
        );

        when(itemRequestClient.getAllOtherUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestResponse);

        mvc.perform(get("/requests/all?from=0&size=4")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestResponseDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestResponseDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestResponseDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$[0].created[3]", is(itemRequestResponseDto.getCreated().getHour())))
                .andExpect(jsonPath("$[0].created[4]", is(itemRequestResponseDto.getCreated().getMinute())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDtoSimpleWithStatus.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemDtoSimpleWithStatus.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDtoSimpleWithStatus.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDtoSimpleWithStatus.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemDtoSimpleWithStatus.getRequestId()), Long.class));
    }

    @Test
    void testGetItemRequestsForUserOwnedItemsWithoutParams() throws Exception {
        itemRequestResponse = new ResponseEntity<>(
                TestUtils.asJsonString(List.of(itemRequestResponseDto)),
                HttpStatus.OK
        );

        when(itemRequestClient.getAllOtherUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestResponse);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestResponseDto.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestResponseDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestResponseDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$[0].created[3]", is(itemRequestResponseDto.getCreated().getHour())))
                .andExpect(jsonPath("$[0].created[4]", is(itemRequestResponseDto.getCreated().getMinute())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDtoSimpleWithStatus.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemDtoSimpleWithStatus.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDtoSimpleWithStatus.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDtoSimpleWithStatus.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemDtoSimpleWithStatus.getRequestId()), Long.class));
    }

    @Test
    void testGetItemRequestsForUserOwnedItemsWrongParams() throws Exception {
        mvc.perform(get("/requests/all?from=-1&size=-999")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Параметры пагинации некорректны.")));
    }

    @Test
    void testGetItemRequestById() throws Exception {
        itemRequestResponse = new ResponseEntity<>(
                TestUtils.asJsonString(itemRequestResponseDto),
                HttpStatus.OK
        );

        when(itemRequestClient.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestResponse);

        mvc.perform(get("/requests/" + 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())))
                .andExpect(jsonPath("$.created[0]", is(itemRequestResponseDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(itemRequestResponseDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(itemRequestResponseDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.created[3]", is(itemRequestResponseDto.getCreated().getHour())))
                .andExpect(jsonPath("$.created[4]", is(itemRequestResponseDto.getCreated().getMinute())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemDtoSimpleWithStatus.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemDtoSimpleWithStatus.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemDtoSimpleWithStatus.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemDtoSimpleWithStatus.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemDtoSimpleWithStatus.getRequestId()), Long.class));
    }
}
