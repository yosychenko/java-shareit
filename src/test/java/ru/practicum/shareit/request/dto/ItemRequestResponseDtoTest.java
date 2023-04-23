package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoSimpleWithStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestResponseDtoTest {
    private static Collection<ItemDtoSimpleWithStatus> items;
    private static Validator validator;
    @Autowired
    private JacksonTester<ItemRequestResponseDto> json;

    @BeforeAll
    public static void beforeAll() {
        items = List.of(
                ItemDtoSimpleWithStatus.builder()
                        .id(1L)
                        .name("item_name")
                        .description("item_description")
                        .available(true)
                        .requestId(100L)
                        .build()
        );

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testItemRequestResponseDto() throws IOException {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("item_request_description")
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .items(items)
                .build();

        JsonContent<ItemRequestResponseDto> result = json.write(itemRequestResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item_request_description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-04-13T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item_name");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("item_description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(100);
    }

    @Test
    void testItemRequestResponseDtoEmptyDescription() throws IOException {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .items(items)
                .build();

        Set<ConstraintViolation<ItemRequestResponseDto>> violations = validator.validate(itemRequestResponseDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemRequestResponseDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание запроса не может быть пустым.");
    }

    @Test
    void testItemRequestResponseDtoTooLongDescription() throws IOException {
        ItemRequestResponseDto itemRequestResponseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("item_request_description".repeat(1000))
                .created(LocalDateTime.of(2023, 4, 13, 10, 0))
                .items(items)
                .build();

        Set<ConstraintViolation<ItemRequestResponseDto>> violations = validator.validate(itemRequestResponseDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemRequestResponseDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Длина описания запроса не может превышать 1000 символов.");
    }
}
