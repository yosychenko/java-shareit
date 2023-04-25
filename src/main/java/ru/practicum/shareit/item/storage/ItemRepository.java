package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findItemsByOwner(User owner);

    List<Item> findItemsByOwnerId(long ownerId, Pageable pageable);

    Collection<Item> findItemsByRequestIn(Collection<ItemRequest> itemRequests);

    Collection<Item> findItemsByRequestInAndOwnerIsNot(Collection<ItemRequest> itemRequests, User requestor);

    @Query(value =
            "SELECT it.id, it.name, it.description, it.available, it.owner, it.request " +
                    "FROM items it " +
                    "WHERE it.available IS TRUE AND (LOWER(it.name) LIKE '%' || ?1 || '%' OR LOWER(it.description) LIKE '%' || ?1 || '%')",
            nativeQuery = true)
    List<Item> searchItems(String query, Pageable pageable);
}
