package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private long id;
    @NotBlank(message = "Описание запроса не может быть пустым.")
    @Size(max = 1000, message = "Длина описания запроса не может превышать 1000 символов.")
    private String description;
    private UserDtoSimple requestor;
    private LocalDateTime created;
}
