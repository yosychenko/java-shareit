package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    private final EntityManager em;
    private final UserService userService;

    private User oldUser;

    @BeforeEach
    void beforeEach() {
        oldUser = new User();
        oldUser.setName("John");
        oldUser.setEmail("john.doe@mail.com");

        em.persist(oldUser);
        em.flush();
    }

    @Test
    void testUpdateUser() {
        long userId = 1L;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Doe");
        updatedUser.setEmail("john.doe@mail.com");

        userService.updateUser(userId, updatedUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", updatedUser.getId()).getSingleResult();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(updatedUser.getId());
        assertThat(user.getName()).isEqualTo(updatedUser.getName());
        assertThat(user.getEmail()).isEqualTo(updatedUser.getEmail());
    }

    @Test
    void testUpdateUserSetOnlyEmail() {
        long userId = 1L;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("john.doemail.com");

        userService.updateUser(userId, updatedUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", updatedUser.getId()).getSingleResult();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(oldUser.getId());
        assertThat(user.getName()).isEqualTo(oldUser.getName());
        assertThat(user.getEmail()).isEqualTo(oldUser.getEmail());
    }

    @Test
    void testUpdateUserSetOnlyName() {
        long userId = 1L;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Doe");

        userService.updateUser(userId, updatedUser);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", updatedUser.getId()).getSingleResult();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(oldUser.getId());
        assertThat(user.getName()).isEqualTo(updatedUser.getName());
        assertThat(user.getEmail()).isEqualTo(oldUser.getEmail());
    }

    @Test
    void testUpdateUserDoesntExist() {
        long userId = 999L;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Doe");
        updatedUser.setEmail("john.doe@mail.com");

        assertThatThrownBy(() -> userService.updateUser(userId, updatedUser))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь c ID=999 не найден.");

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.id = :id", User.class);
        assertThatThrownBy(() -> query1.setParameter("id", updatedUser.getId()).getSingleResult())
                .isInstanceOf(NoResultException.class);

        TypedQuery<User> query2 = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query2.setParameter("id", oldUser.getId()).getSingleResult();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(oldUser.getId());
        assertThat(user.getName()).isEqualTo(oldUser.getName());
        assertThat(user.getEmail()).isEqualTo(oldUser.getEmail());
    }
}
