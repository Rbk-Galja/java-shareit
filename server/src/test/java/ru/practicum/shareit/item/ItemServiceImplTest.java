package ru.practicum.shareit.item;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@ExtendWith(SpringExtension.class)
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImplTest {
    final ItemRepository itemRepository;
    final TestEntityManager manager;
    User user;
    Item item;
    Item item2;
    User user2;
    Item item3;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        user = manager.persistFlushFind(User.builder()
                .name("name")
                .email("mail@mail.ru")
                .build());

        item = manager.persistFlushFind(Item.builder()
                .name("дрель")
                .description("дрель ударная")
                .available(true)
                .owner(user)
                .build());

        item2 = manager.persistFlushFind(Item.builder()
                .name("Фотоаппарат")
                .description("Фотоаппарат зеркальный")
                .owner(user)
                .available(true)
                .build());

        user2 = manager.persistFlushFind(User.builder()
                .name("name2")
                .email("mail2@mail.ru")
                .build());

        item3 = manager.persistFlushFind(Item.builder()
                .name("Стол")
                .description("Стол походный")
                .owner(user2)
                .available(true)
                .build());

        comment = Comment.builder()
                .text("Гуд")
                .item(item2)
                .author(user)
                .created(Instant.now())
                .build();

    }

    @DisplayName("Сохранение и получение Item пользователя")
    @Test
    void saveAndGetItemsByOwner() {
        List<Item> itemsByOwner = itemRepository.findByOwner(user);

        assertThat(itemsByOwner.size(), equalTo(2));
        assertThat(itemsByOwner.getFirst(), equalTo(item));
        assertThat(itemsByOwner.get(1), equalTo(item2));
    }

    @DisplayName("Поиск вещи по имени и описанию")
    @Test
    void searchItemByText() {
        List<Item> searchItem = itemRepository.searchItem("стол");

        assertThat(searchItem.size(), equalTo(1));
        assertThat(searchItem.getFirst(), equalTo(item3));
    }

    @DisplayName("Добавление комментария")
    @Test
    void addComment() {
        Comment addComment = manager.persistFlushFind(comment);

        assertThat(addComment.getId(), notNullValue());
        assertThat(addComment.getItem(), equalTo(comment.getItem()));
        assertThat(addComment.getText(), equalTo(comment.getText()));
        assertThat(addComment.getAuthor(), equalTo(comment.getAuthor()));
        assertThat(addComment.getCreated().truncatedTo(ChronoUnit.SECONDS),
                equalTo(comment.getCreated().truncatedTo(ChronoUnit.SECONDS)));
    }

}
