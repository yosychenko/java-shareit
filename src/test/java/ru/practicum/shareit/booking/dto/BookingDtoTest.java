package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
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
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;
    private static ItemDtoSimple itemDtoSimple;
    private static UserDtoSimple userDtoSimple;
    private static Validator validator;

    @BeforeAll
    public static void beforeAll() {
        itemDtoSimple = ItemDtoSimple.builder()
                .id(10L)
                .name("item_name")
                .build();
        userDtoSimple = UserDtoSimple.builder()
                .id(23L)
                .name("user_name")
                .build();

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testBookingDto() throws IOException {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .item(itemDtoSimple)
                .booker(userDtoSimple)
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-04-13T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-04-20T10:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item_name");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(23);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user_name");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testBookingDtoEmptyStart() throws IOException {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .end(LocalDateTime.of(2023, 4, 20, 10, 0))
                .item(itemDtoSimple)
                .booker(userDtoSimple)
                .status(BookingStatus.WAITING)
                .build();


        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<BookingDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("start");
        assertThat(violation.getMessage()).isEqualTo("Дата начала брони не может быть пустой.");
    }

    @Test
    void testBookingDtoEmptyEnd() throws IOException {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 4, 13, 10, 0))
                .item(itemDtoSimple)
                .booker(userDtoSimple)
                .status(BookingStatus.WAITING)
                .build();


        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertThat(violations.isEmpty()).isFalse();

        ConstraintViolation<BookingDto> violation = violations.stream().findFirst().get();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("end");
        assertThat(violation.getMessage()).isEqualTo("Дата окончания брони не может быть пустой.");
    }
}
