package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestStorage;
    private final UserService userService;


    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestStorage, UserService userService) {
        this.itemRequestStorage = itemRequestStorage;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ItemRequest createItemRequest(long userId, ItemRequest newItemRequest) {
        User requestor = userService.getUserById(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestor(requestor);
        itemRequest.setDescription(newItemRequest.getDescription());

        return itemRequestStorage.save(itemRequest);
    }

    @Override
    public Collection<ItemRequest> getAllItemRequests(long userId) {
        return null;
    }

    @Override
    public Collection<ItemRequest> getItemRequestsPaginated(long userId, int from, int size) {
        return null;
    }

    @Override
    public ItemRequest getItemRequestById(long userId, long itemRequestId) {
        return null;
    }
}
