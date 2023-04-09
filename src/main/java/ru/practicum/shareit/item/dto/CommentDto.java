package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;

    @NotBlank(message = "Текст комментария не должен быть пустым.")
    @Size(max = 1000, message = "Длина комментария не может превышать 1000 символов.")
    private String text;

    private String authorName;

    private LocalDateTime created;
}
