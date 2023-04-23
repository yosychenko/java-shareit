package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingTimeIntervalDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    private static BookingTimeIntervalDto bookingTimeIntervalDtoLast;
    private static BookingTimeIntervalDto bookingTimeIntervalDtoNext;
    private static CommentDto commentDto;
    private static Validator validator;
    @Autowired
    private JacksonTester<ItemDto> json;

    @BeforeAll
    public static void beforeAll() {
        bookingTimeIntervalDtoLast = BookingTimeIntervalDto.builder()
                .id(10L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .bookerId(1L)
                .build();
        bookingTimeIntervalDtoNext = BookingTimeIntervalDto.builder()
                .id(10L)
                .start(LocalDateTime.of(2023, 5, 13, 10, 0))
                .end(LocalDateTime.of(2023, 5, 20, 10, 0))
                .bookerId(1L)
                .build();
        commentDto = CommentDto.builder()
                .id(100L)
                .text("comment")
                .authorName("author")
                .created(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .description("item_description")
                .available(true)
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(150L)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item_name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item_description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2023-04-13T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2023-04-20T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2023-05-13T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2023-05-20T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2023-04-20T10:00:00");
    }

    @Test
    void testItemDtoEmptyName() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .description("item_description")
                .available(true)
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(150L)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Название вещи не может быть пустым.");
    }

    @Test
    void testItemDtoNameTooLong() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name".repeat(255))
                .description("item_description")
                .available(true)
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(150L)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("Длина названия вещи не может превышать 255 символов.");
    }

    @Test
    void testItemDtoEmptyDescription() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .available(true)
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(150L)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Описание вещи не может быть пустым.");
    }

    @Test
    void testItemDtoDescriptionTooLong() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .description("desc".repeat(1000))
                .available(true)
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(150L)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).isEqualTo("Длина описания вещи не может превышать 1000 символов.");
    }

    @Test
    void testItemDtoEmptyAvailabilityStatus() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item_name")
                .description("desc")
                .lastBooking(bookingTimeIntervalDtoLast)
                .nextBooking(bookingTimeIntervalDtoNext)
                .comments(List.of(commentDto))
                .requestId(150L)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<ItemDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("available");
        assertThat(violation.getMessage()).isEqualTo("Статус доступности вещи должен быть указан.");
    }
}
