package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(message = "Имя или логин не может быть пустым.")
    @Size(max = 255, message = "Длина имени или логина не может превышать 255 символов.")
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(message = "Некорректный формат электронной почты.")
    @Size(max = 512, message = "Длина электронной почты не может превышать 512 символов.")
    private String email;
}
