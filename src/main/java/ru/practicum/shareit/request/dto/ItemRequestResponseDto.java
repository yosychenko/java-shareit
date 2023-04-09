package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserDtoSimple;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ItemRequestResponseDto {
    private long id;
    @NotBlank(message = "Описание запроса не может быть пустым.")
    @Size(max = 1000, message = "Длина описания запроса не может превышать 1000 символов.")
    private String description;
}
