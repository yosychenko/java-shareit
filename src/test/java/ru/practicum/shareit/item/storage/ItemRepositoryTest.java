package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;

    private Item item;

    private Item itemNotAvail;

    private Item item2;

    private Item item3;

    private Item item4NotAvail;

    private ItemRequest itemRequest;

    @BeforeEach
    public void beforeEach() {
        User requestor = new User();
        requestor.setName("Booker");
        requestor.setEmail("john.booker@mail.com");

        owner = new User();
        owner.setName("John");
        owner.setEmail("john.doe@mail.com");

        itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.of(2023, 4, 13, 10, 0));

        item = new Item();
        item.setName("Отвертка");
        item.setDescription("С Аккумулятором");
        item.setAvailable(true);
        item.setOwner(owner);

        itemNotAvail = new Item();
        itemNotAvail.setName("Отвертка");
        itemNotAvail.setDescription("Обычная");
        itemNotAvail.setAvailable(false);
        itemNotAvail.setOwner(owner);

        item2 = new Item();
        item2.setName("Ключ");
        item2.setDescription("на 13");
        item2.setAvailable(true);
        item2.setOwner(owner);

        item3 = new Item();
        item3.setName("Ключ");
        item3.setDescription("на 15");
        item3.setAvailable(true);
        item3.setOwner(owner);

        item4NotAvail = new Item();
        item4NotAvail.setName("Ключ");
        item4NotAvail.setDescription("на 17");
        item4NotAvail.setAvailable(false);
        item4NotAvail.setOwner(owner);

        em.persist(owner);
        em.persist(requestor);
        em.persist(itemRequest);
        em.flush();

    }

    @Test
    void testCreateItem() {
        Item itemWithRequest = new Item();
        itemWithRequest.setName("Отвертка c Запросом");
        itemWithRequest.setDescription("С Аккумулятором и Запросом");
        itemWithRequest.setAvailable(true);
        itemWithRequest.setOwner(owner);
        itemWithRequest.setRequest(itemRequest);

        itemRepository.save(itemWithRequest);

        long itemId = 1L;

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemFromDb = query.setParameter("id", itemId).getSingleResult();

        assertThat(itemFromDb).isNotNull();
        assertThat(itemFromDb.getId()).isEqualTo(itemId);
        assertThat(itemFromDb.getName()).isEqualTo(itemWithRequest.getName());
        assertThat(itemFromDb.getDescription()).isEqualTo(itemWithRequest.getDescription());
        assertThat(itemFromDb.getAvailable()).isEqualTo(itemWithRequest.getAvailable());
        assertThat(itemFromDb.getOwner()).isEqualTo(itemWithRequest.getOwner());
        assertThat(itemFromDb.getRequest()).isEqualTo(itemWithRequest.getRequest());
    }

    @Test
    void testSearchItemsFindOne() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4NotAvail);
        em.flush();

        List<Item> foundItems = itemRepository.searchItems("отвертка", PageRequest.of(0, 2000));

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems).containsAll(List.of(item));
        assertThat(foundItems).doesNotContain(itemNotAvail);
    }

    @Test
    void testSearchItemsFindMany() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4NotAvail);
        em.flush();

        List<Item> foundItems = itemRepository.searchItems("ключ", PageRequest.of(0, 2000));

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).containsAll(List.of(item2, item3));
        assertThat(foundItems).doesNotContain(item4NotAvail);
    }

    @Test
    void testSearchFindByDescription() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4NotAvail);
        em.flush();

        List<Item> foundItems = itemRepository.searchItems("на 15", PageRequest.of(0, 2000));

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).hasSize(1);
        assertThat(foundItems).containsAll(List.of(item3));
        assertThat(foundItems).doesNotContain(item4NotAvail);
    }

    @Test
    void testSearchFindNothing() {
        em.persist(item);
        em.persist(itemNotAvail);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4NotAvail);
        em.flush();

        List<Item> foundItems = itemRepository.searchItems("монитор", PageRequest.of(0, 2000));

        assertThat(foundItems).isEmpty();
    }
}
