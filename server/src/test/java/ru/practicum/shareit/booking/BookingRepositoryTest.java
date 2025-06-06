package ru.practicum.shareit.booking;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@ExtendWith(SpringExtension.class)
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRepositoryTest {
    final BookingRepository bookingRepository;
    final TestEntityManager manager;
    User owner;
    User userBooker;
    Item itemForBooking;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        owner = manager.persistFlushFind(User.builder().name("nameOwner").email("mail9@mail.ru").build());
        itemForBooking = manager.persistFlushFind(Item.builder()
                .name("уровень строиельный")
                .description("строительный уровень 1м")
                .available(true)
                .owner(owner)
                .build());
        userBooker = manager.persistFlushFind(User.builder().name("nameBooker").email("mail24@mail.ru").build());
        booking = manager.persistFlushFind(Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(itemForBooking)
                .booker(userBooker)
                .status(Status.WAITING)
                .build());

    }

    @DisplayName("Получение бронирования по id забронировавшего пользователя")
    @Test
    void getByBookerId() {
        List<Booking> bookingBooker = bookingRepository.findByBooker_id(userBooker.getId());

        assertThat(bookingBooker.getFirst().getBooker(), equalTo(userBooker));
    }
}
