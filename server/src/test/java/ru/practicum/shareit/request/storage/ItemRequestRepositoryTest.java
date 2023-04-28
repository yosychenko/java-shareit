package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ContextConfiguration(classes = ShareItServer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestStorage;

    private User requestor;

    @BeforeEach
    void beforeEach() {
        requestor = new User();
        requestor.setName("Booker");
        requestor.setEmail("john.booker@mail.com");

        em.persist(requestor);
        em.flush();
    }

    @Test
    void testCreateItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.of(2023, 4, 13, 10, 0));

        itemRequestStorage.save(itemRequest);

        long itemRequestId = 1L;

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequestFromDb = query.setParameter("id", itemRequestId).getSingleResult();

        assertThat(itemRequestFromDb).isNotNull();
        assertThat(itemRequestFromDb.getId()).isEqualTo(itemRequestId);
        assertThat(itemRequestFromDb.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestFromDb.getRequestor()).isEqualTo(itemRequest.getRequestor());
        assertThat(itemRequestFromDb.getCreated()).isEqualTo(itemRequest.getCreated());
    }

    @Test
    void testCreateItemRequestWithNullDescription() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.of(2023, 4, 13, 10, 0));

        assertThatThrownBy(() -> itemRequestStorage.save(itemRequest)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
