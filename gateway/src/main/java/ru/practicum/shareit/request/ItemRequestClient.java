package ru.practicum.shareit.request;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

public class ItemRequestClient extends BaseClient {
    public ItemRequestClient(RestTemplate rest) {
        super(rest);
    }
}
