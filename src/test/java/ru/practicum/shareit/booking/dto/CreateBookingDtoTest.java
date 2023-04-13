package ru.practicum.shareit.booking.dto;

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
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CreateBookingDtoTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;
    private static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testCreateBookingDto() throws IOException {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();

        JsonContent<CreateBookingDto> result = json.write(createBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-04-13T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-04-20T10:00:00");
    }

    @Test
    void testCreateBookingDtoEmptyItemId() throws IOException {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(createBookingDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<CreateBookingDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("itemId");
        assertThat(violation.getMessage()).isEqualTo("Идентификатор вещи для брони не может быть пустым.");
    }

    @Test
    void testCreateBookingDtoEmptyStart() throws IOException {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .build();

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(createBookingDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<CreateBookingDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("start");
        assertThat(violation.getMessage()).isEqualTo("Дата начала брони не может быть пустой.");
    }

    @Test
    void testCreateBookingDtoEmptyEnd() throws IOException {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .build();

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(createBookingDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<CreateBookingDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("end");
        assertThat(violation.getMessage()).isEqualTo("Дата окончания брони не может быть пустой.");
    }
}
