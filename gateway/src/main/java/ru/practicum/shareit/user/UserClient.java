package ru.practicum.shareit.user;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

public class UserClient extends BaseClient {
    public UserClient(RestTemplate rest) {
        super(rest);
    }
}
