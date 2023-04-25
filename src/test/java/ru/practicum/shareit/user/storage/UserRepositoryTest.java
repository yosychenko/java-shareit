package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john.doe@mail.com");

        userRepository.save(user);

        long userId = 1L;

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userFromDb = query.setParameter("id", userId).getSingleResult();

        assertThat(userFromDb).isNotNull();
        assertThat(userFromDb.getId()).isEqualTo(userId);
        assertThat(userFromDb.getName()).isEqualTo(user.getName());
        assertThat(userFromDb.getEmail()).isEqualTo(user.getEmail());
    }
}
