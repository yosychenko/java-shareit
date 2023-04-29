package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.ExceptionControllerAdvice;
import ru.practicum.shareit.TestUtils;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    private MockMvc mvc;

    private UserDto userDto;

    private ResponseEntity<Object> userResponse;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ExceptionControllerAdvice.class)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .build();

        userResponse = new ResponseEntity<>(
                TestUtils.asJsonString(userDto),
                HttpStatus.OK
        );
    }


    @Test
    void testCreateUser() throws Exception {
        when(userClient.createUser(any(UserDto.class))).thenReturn(userResponse);

        mvc.perform(post("/users")
                        .content(TestUtils.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

    }

    @Test
    void testCreateUserEmptyEmailValidationError() throws Exception {
        userDto.setName("updateName");

        userResponse = new ResponseEntity<>(
                Map.of("email", "Электронная почта не может быть пустой."),
                HttpStatus.BAD_REQUEST
        );

        when(userClient.createUser(any(UserDto.class))).thenReturn(userResponse);

        mvc.perform(post("/users")
                        .content(TestUtils.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("email")))
                .andExpect(jsonPath("$.email", is("Электронная почта не может быть пустой.")));
    }

    @Test
    void testUpdateUser() throws Exception {
        userDto.setName("updateName");

        userResponse = new ResponseEntity<>(
                TestUtils.asJsonString(userDto),
                HttpStatus.OK
        );

        when(userClient.updateUser(anyLong(), any(UserDto.class))).thenReturn(userResponse);

        mvc.perform(patch("/users/" + 1L)
                        .content(TestUtils.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));


    }

    @Test
    void testUpdateUserUserDoesntExist() throws Exception {
        userResponse = new ResponseEntity<>(
                Map.of("message", "Пользователь c ID=1 не найден."),
                HttpStatus.NOT_FOUND
        );

        when(userClient.updateUser(anyLong(), any(UserDto.class))).thenReturn(userResponse);

        mvc.perform(patch("/users/" + 1L)
                        .content(TestUtils.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Пользователь c ID=1 не найден.")));
    }

    @Test
    void testDeleteUser() throws Exception {
        userResponse = new ResponseEntity<>(
                null,
                HttpStatus.OK
        );

        when(userClient.deleteUser(anyLong())).thenReturn(userResponse);

        mvc.perform(delete("/users/" + 1L)
                        .content(TestUtils.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserUserDoesntExist() throws Exception {
        userResponse = new ResponseEntity<>(
                Map.of("message", "Пользователь c ID=1 не найден."),
                HttpStatus.NOT_FOUND
        );

        when(userClient.deleteUser(anyLong())).thenReturn(userResponse);

        mvc.perform(delete("/users/" + 1L)
                        .content(TestUtils.asJsonString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Пользователь c ID=1 не найден.")));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userClient.getUserById(anyLong())).thenReturn(userResponse);

        mvc.perform(get("/users/" + 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }


    @Test
    void testGetUserByIdUserNotFound() throws Exception {
        userResponse = new ResponseEntity<>(
                Map.of("message", "Пользователь c ID=1 не найден."),
                HttpStatus.NOT_FOUND
        );

        when(userClient.getUserById(anyLong())).thenReturn(userResponse);

        mvc.perform(get("/users/" + 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("message")))
                .andExpect(jsonPath("$.message", is("Пользователь c ID=1 не найден.")));
    }

    @Test
    void testGetAllUsers() throws Exception {
        userResponse = new ResponseEntity<>(
                TestUtils.asJsonString(List.of(userDto)),
                HttpStatus.OK
        );

        when(userClient.getAllUsers()).thenReturn(userResponse);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void testGetAllUsersNoUsers() throws Exception {
        userResponse = new ResponseEntity<>(
                TestUtils.asJsonString(List.of()),
                HttpStatus.OK
        );

        when(userClient.getAllUsers()).thenReturn(userResponse);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

}
