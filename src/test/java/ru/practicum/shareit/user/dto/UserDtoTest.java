package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    private static Validator validator;
    @Autowired
    private JacksonTester<UserDto> json;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user_name")
                .email("email@gmail.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user_name");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@gmail.com");
    }

    @Test
    void testUserDtoEmptyName() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("email@gmail.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<UserDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Имя или логин не может быть пустым.");
    }

    @Test
    void testUserDtoNameTooLong() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user_name".repeat(255))
                .email("email@gmail.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<UserDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Длина имени или логина не может превышать 255 символов.");
    }

    @Test
    void testUserDtoEmptyEmail() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user_name")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<UserDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Электронная почта не может быть пустой.");
    }

    @Test
    void testUserDtoWrongEmail() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user_name")
                .email("emailgmail.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<UserDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("email");
        assertThat(violation.getMessage()).isEqualTo("Некорректный формат электронной почты.");
    }
}
