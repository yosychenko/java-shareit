package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {

    @Mock
    private ItemRequestRepository itemRequestStorage;

    @Mock
    private ItemRepository itemStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User owner;

    private User requestor;

    private Item item;

    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        owner = new User();
        owner.setId(10L);
        owner.setName("John Owner");
        owner.setEmail("john.owner@mail.com");

        requestor = new User();
        requestor.setId(25L);
        requestor.setName("Booker");
        requestor.setEmail("john.booker@mail.com");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.of(2023, 4, 13, 10, 0));

        item = new Item();
        item.setId(10L);
        item.setName("item_name");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);
    }

    @Test
    void testCreateItemRequest() {
        ItemRequest newItemRequest = new ItemRequest();
        newItemRequest.setDescription("description");

        when(userService.getUserById(anyLong())).thenReturn(requestor);
        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest createdItemRequest = itemRequestService.createItemRequest(requestor.getId(), newItemRequest);

        assertThat(createdItemRequest).isNotNull();
        assertThat(createdItemRequest.getId()).isEqualTo(itemRequest.getId());
        assertThat(createdItemRequest.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(createdItemRequest.getRequestor()).isEqualTo(itemRequest.getRequestor());
    }

    @Test
    void testGetItemRequestsForUserOwnedItems() {
        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(itemStorage.findItemsByOwnerAndRequestIsNotNull(any(User.class))).thenReturn(List.of(item));

        Collection<ItemRequestResponseDto> resultRequests = itemRequestService.getItemRequestsForUserOwnedItems(owner.getId(), PageRequest.of(0, 2000));

        assertThat(resultRequests).isNotEmpty();
        assertThat(resultRequests).contains(ItemRequestMapper.toItemRequestResponseDto(itemRequest, List.of(item)));
    }

    @Test
    void testGetItemRequestByIdFullInfo() {
        when(userService.getUserById(anyLong())).thenReturn(requestor);
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemStorage.findItemsByRequestIn(anyCollection())).thenReturn(List.of(item));

        ItemRequestResponseDto foundItemRequest = itemRequestService.getItemRequestByIdFullInfo(requestor.getId(), itemRequest.getId());

        assertThat(foundItemRequest).isNotNull();
        assertThat(foundItemRequest).isEqualTo(ItemRequestMapper.toItemRequestResponseDto(itemRequest, List.of(item)));
    }
}
