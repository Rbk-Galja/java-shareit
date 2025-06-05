package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ExtendWith(SpringExtension.class)
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImplTest {

    final UserRepository userRepository;
    final TestEntityManager manager;
    User request;


    @BeforeEach
    void beforeEach() {
        request = User.builder()
                .name("name")
                .email("mail@mail.ru")
                .build();
    }

    @DisplayName("Сохранение и получение по айди User")
    @Test
    void saveAndGetUser() {
        User user = manager.persistFlushFind(request);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(request.getName()));
        assertThat(user.getEmail(), equalTo(request.getEmail()));
    }

    @DisplayName("Обновление User")
    @Test
    void updateUser() {
        long id = (long) manager.persistAndGetId(request);
        User update = User.builder().id(id).name("newName").email(request.getEmail()).build();
        User user = userRepository.save(update);
        manager.flush();

        assertThat(user.getName(), equalTo("newName"));

        List<User> users = userRepository.findAll();
        manager.flush();

        assertThat(users.size(), equalTo(1));
    }

    @DisplayName("Удаление User")
    @Test
    void deleteUser() {
        User user = manager.persistFlushFind(request);

        assertThat(user.getId(), notNullValue());

        manager.remove(user);
        Optional<User> userDelete = userRepository.findById(user.getId());
        manager.flush();

        assertThat(userDelete.isEmpty(), equalTo(true));
    }

    @DisplayName("Получение списка User")
    @Test
    void getAllUser() {
        User request2 = User.builder().name("name2").email("mail2@mail.ru").build();
        manager.persist(request2);
        manager.persist(request);

        List<User> users = userRepository.findAll();
        manager.flush();

        assertThat(users.size(), equalTo(2));
    }

    @DisplayName("Проверка на дублирование email")
    @Test
    void testDuplicateEmail() {
        // Сохраняем первого пользователя
        User firstUser = manager.persistFlushFind(request);

        // Пытаемся сохранить второго пользователя с тем же email
        User secondUser = User.builder()
                .name("anotherName")
                .email(firstUser.getEmail())
                .build();

        // Проверяем, что возникает исключение при попытке сохранить дубликат
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(secondUser);
            manager.flush();
        });
    }

}
