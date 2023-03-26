package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;

    @NotBlank(message = "Текст комментария не должен быть пустым.")
    private String text;

    private Item item;

    private User author;

    private LocalDateTime created;
}
