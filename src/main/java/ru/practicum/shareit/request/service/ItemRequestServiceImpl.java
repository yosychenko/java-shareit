package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestStorage;
    private final ItemRepository itemStorage;
    private final UserService userService;


    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestStorage, UserService userService, ItemRepository itemStorage) {
        this.itemRequestStorage = itemRequestStorage;
        this.userService = userService;
        this.itemStorage = itemStorage;
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
    @Transactional(readOnly = true)
    public Collection<ItemRequestResponseDto> getAllItemRequestsFromUser(long userId) {
        User requestor = userService.getUserById(userId);
        Collection<ItemRequest> userRequests = itemRequestStorage.findItemRequestsByRequestor(requestor);
        Collection<Item> requestedItems = itemStorage.findItemsByRequestInAndOwnerIsNot(userRequests, requestor);

        if (requestedItems.size() > 0) {
            return getItemRequestsWithResponses(requestedItems);
        }

        return userRequests.stream()
                .map(request -> ItemRequestMapper.toItemRequestResponseDto(request, requestedItems))
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestResponseDto> getAllOtherUsersRequests(long userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        Collection<ItemRequest> otherUsersRequests = itemRequestStorage.findItemRequestsByRequestorNot(user, pageable);
        Collection<Item> otherUsersRequestedItems = itemStorage.findItemsByRequestIn(otherUsersRequests);

        return getItemRequestsWithResponses(otherUsersRequestedItems);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto getItemRequestByIdFullInfo(long userId, long itemRequestId) {
        ItemRequest request = getItemRequestById(itemRequestId);
        Collection<Item> requestedItems = itemStorage.findItemsByRequestIn(List.of(request));

        return ItemRequestMapper.toItemRequestResponseDto(request, requestedItems);
    }

    @Override
    public ItemRequest getItemRequestById(long itemRequestId) {
        return itemRequestStorage.findById(itemRequestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(itemRequestId));
    }

    private Collection<ItemRequestResponseDto> getItemRequestsWithResponses(Collection<Item> requestedItems) {
        Map<ItemRequest, List<Item>> requestToItemsMap = requestedItems
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));

        Collection<ItemRequestResponseDto> result = new ArrayList<>();
        for (var entry : requestToItemsMap.entrySet()) {
            result.add(ItemRequestMapper.toItemRequestResponseDto(entry.getKey(), entry.getValue()));
        }
        return result;
    }

}
