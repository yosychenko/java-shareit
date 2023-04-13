package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testCommentDto() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("author")
                .created(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-04-20T10:00:00");
    }

    @Test
    void testCommentDtoLongText() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("a".repeat(1001))
                .authorName("author")
                .created(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();


        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<CommentDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("text");
        assertThat(violation.getMessage()).isEqualTo("Длина комментария не может превышать 1000 символов.");
    }
}
