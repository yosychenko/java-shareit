package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoSimpleWithStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTest {
    private final EntityManager em;

    private final ItemRequestService itemRequestService;

    private Item item;

    private ItemRequest itemRequest;


    @BeforeEach
    void beforeEach() {
        User owner = new User();
        owner.setName("John Owner");
        owner.setEmail("john.owner@mail.com");

        User user = new User();
        user.setName("John");
        user.setEmail("john.doe@mail.com");

        User userWithoutRequests = new User();
        userWithoutRequests.setName("John Withoutreqeustov");
        userWithoutRequests.setEmail("john.owner@mail.com");

        itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужна отвертка с аккумулятором");
        itemRequest.setRequestor(user);

        item = new Item();
        item.setName("Отвертка 1");
        item.setDescription("С Аккумулятором");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);


        em.persist(owner);
        em.persist(user);
        em.persist(userWithoutRequests);
        em.persist(itemRequest);
        em.persist(item);
        em.flush();
    }

    @Test
    void testGetAllItemRequestsFromUser() {
        ItemRequestResponseDto correctRequest = ItemRequestMapper.toItemRequestResponseDto(itemRequest, List.of(item));

        ArrayList<ItemRequestResponseDto> result = new ArrayList<>(itemRequestService.getAllItemRequestsFromUser(2L));
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        ItemRequestResponseDto resultDto = result.get(0);
        assertThat(resultDto.getId()).isEqualTo(correctRequest.getId());
        assertThat(resultDto.getDescription()).isEqualTo(correctRequest.getDescription());

        ArrayList<ItemDtoSimpleWithStatus> items = new ArrayList<>(resultDto.getItems());
        assertThat(resultDto.getItems()).isNotEmpty();
        assertThat(resultDto.getItems()).hasSize(1);

        ItemDtoSimpleWithStatus requestedItem = items.get(0);
        ItemDtoSimpleWithStatus correctRequestedItem = new ArrayList<>(correctRequest.getItems()).get(0);
        assertThat(requestedItem.getId()).isEqualTo(correctRequestedItem.getId());
        assertThat(requestedItem.getName()).isEqualTo(correctRequestedItem.getName());
        assertThat(requestedItem.getDescription()).isEqualTo(correctRequestedItem.getDescription());
        assertThat(requestedItem.getAvailable()).isEqualTo(correctRequestedItem.getAvailable());
        assertThat(requestedItem.getRequestId()).isEqualTo(correctRequestedItem.getRequestId());
    }

    @Test
    void testGetAllItemRequestsFromUserUserHasNoRequests() {
        ArrayList<ItemRequestResponseDto> result = new ArrayList<>(itemRequestService.getAllItemRequestsFromUser(3L));
        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllItemRequestsFromUserUserDoesNotExist() {
        assertThatThrownBy(() -> itemRequestService.getAllItemRequestsFromUser(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь c ID=999 не найден.");

    }
}