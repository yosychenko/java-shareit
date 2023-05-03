package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.exception.CannotLeaveCommentException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.UserIsNotOwnerException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateEmailExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler({
            CannotBookUnavailableItemException.class,
            SameApproveStatusException.class,
            CannotLeaveCommentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            ItemNotFoundException.class,
            UserIsNotOwnerException.class,
            BookingNotFoundException.class,
            UserHasNoAccessToBookingException.class,
            CannotApproveBookingException.class,
            CannotBookOwnedItemException.class,
            ItemRequestNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExceptions(RuntimeException ex) {
        return Map.of("message", ex.getMessage());
    }
}
