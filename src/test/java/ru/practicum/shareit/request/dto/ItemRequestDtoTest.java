package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDtoSimple;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    private static UserDtoSimple userDtoSimple;
    private static Validator validator;
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @BeforeAll
    public static void beforeAll() {
        userDtoSimple = UserDtoSimple.builder()
                .id(23L)
                .name("user_name")
                .build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testItemRequestDto() throws IOException {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("item_request_description")
                .requestor(userDtoSimple)
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item_request_description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(23);
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("user_name");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-04-13T10:00:00");
    }

    @Test
    void testItemRequestDtoEmptyDescription() throws IOException {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .requestor(userDtoSimple)
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemRequestDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание запроса не может быть пустым.");
    }

    @Test
    void testItemRequestDtoTooLongDescription() throws IOException {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("item_request_description".repeat(1000))
                .requestor(userDtoSimple)
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemRequestDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Длина описания запроса не может превышать 1000 символов.");
    }
}
