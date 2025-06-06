package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessAddCommentException;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mapper.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemServiceImplTest {
    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    final long itemId = 1L;
    final long userId = 1L;
    final long bookerId = 2L;
    final Item item = Item.builder().id(itemId).name("Test Item").build();
    final User owner = User.builder().id(userId).build();
    final List<Item> items = List.of(
            Item.builder().id(1L).name("Item 1").owner(owner).build(),
            Item.builder().id(2L).name("Item 2").owner(owner).build()
    );
    final NewItemRequest request = NewItemRequest.builder().name("New Item").description("Description").build();
    final UpdateItemRequest requestUpdate = UpdateItemRequest.builder().name("Updated Name")
            .description("Updated Description").build();
    final UserDto booker = UserDto.builder().id(bookerId).name("Вася").email("mail@ya.ru").build();
    final NewCommentRequest requestComment = NewCommentRequest.builder().text("New comment").build();

    final Comment savedComment = Comment.builder().id(1L).text("New comment").item(item)
            .author(UserDtoMapper.mapToUser(booker)).build();

    @DisplayName("Получение Item по id")
    @Test
    void testGetById() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDtoBooking result = itemService.getById(itemId);

        assertEquals(item.getName(), result.getName());
        verify(itemRepository).findById(itemId);
    }

    @DisplayName("Получение по id несуществующей вещи")
    @Test
    void testGetByIdNotFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(itemId));
    }

    @DisplayName("Получение Item по id пользователя")
    @Test
    void testGetUserItems() {
        when(userService.getById(userId)).thenReturn(UserDtoMapper.mapToUserDto(owner));
        when(itemRepository.findByOwner(owner)).thenReturn(items);

        List<ItemDto> result = itemService.getUserItems(userId);

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
    }

    @DisplayName("Добавление валидной вещи")
    @Test
    void testAddItem() {
        when(userService.getById(userId)).thenReturn(UserDtoMapper.mapToUserDto(owner));

        ItemDto result = itemService.add(userId, request);

        assertNotNull(result.getId());
        assertEquals("New Item", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @DisplayName("Обновление валидной вещи")
    @Test
    void testUpdateItem() {
        Item oldItem = Item.builder().id(itemId).name("Old Name").owner(owner).build();
        when(itemRepository.save(oldItem)).thenReturn(oldItem);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        ItemDto result = itemService.update(userId, itemId, requestUpdate);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        verify(itemRepository).save(any(Item.class));
    }

    @DisplayName("Обновление несуществующей вещи")
    @Test
    void testUpdateNotFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(userId, itemId, requestUpdate));
    }

    @DisplayName("Обновление вещи без права доступа")
    @Test
    void testUpdateItemNoAccess() {
        User owner2 = new User();
        owner.setId(2L);
        Item oldItem = Item.builder().id(itemId).name("Old Name").owner(owner2).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        assertThrows(NoAccessException.class, () -> itemService.update(userId, itemId, requestUpdate));
    }

    @DisplayName("Поиск вещи по текстовому запросу")
    @Test
    void testSearchItem() {
        String searchQuery = "test";
        User owner1 = User.builder().id(1L).build();
        User owner2 = User.builder().id(2L).build();
        List<Item> items = List.of(
                Item.builder().id(1L).name("Test Item 1").owner(owner1).build(),
                Item.builder().id(2L).name("Another Test").owner(owner2).build()
        );

        when(itemRepository.searchItem(searchQuery)).thenReturn(items);

        List<ItemDto> result = itemService.searchItems(searchQuery);

        assertEquals(2, result.size());
        assertEquals("Test Item 1", result.get(0).getName());
        assertEquals("Another Test", result.get(1).getName());
    }

    @Test
    @DisplayName("Поиск с пустым запросом")
    void testSearchEmptyQuery() {
        String emptyQuery = "";

        List<ItemDto> result = itemService.searchItems(emptyQuery);

        assertTrue(result.isEmpty());

        verify(itemRepository, never()).searchItem(emptyQuery);
    }

    @Test
    @DisplayName("Поиск с пустым запросом из пробелов")
    void testSearchBlankQuery() {
        String emptyQuery = "    ";

        List<ItemDto> result = itemService.searchItems(emptyQuery);

        assertTrue(result.isEmpty());

        verify(itemRepository, never()).searchItem(emptyQuery);
    }

    @DisplayName("Добавление комментария")
    @Test
    void testAddCommentValid() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.getById(bookerId)).thenReturn(booker);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 5, 5, 11, 11, 11))
                .end(LocalDateTime.of(2025, 5, 7, 11, 11, 11))
                .booker(UserDtoMapper.mapToUser(booker))
                .item(item)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findByItemAndBooker(item, UserDtoMapper.mapToUser(booker))).thenReturn(booking);

        CommentDto result = itemService.addComment(requestComment, itemId, bookerId);

        assertNotNull(result.getId());
        assertEquals("New comment", result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @DisplayName("Ошибка добавления комментария до истечения срока бронирования")
    @Test
    void testAddCommentWhenBookingIsPresent() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.getById(bookerId)).thenReturn(booker);

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 5, 5, 11, 11, 11))
                .end(LocalDateTime.of(2025, 9, 7, 11, 11, 11))
                .booker(UserDtoMapper.mapToUser(booker))
                .item(item)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findByItemAndBooker(item, UserDtoMapper.mapToUser(booker))).thenReturn(booking);

        assertThrows(NoAccessAddCommentException.class, () -> itemService.addComment(requestComment, itemId, bookerId));
    }

    @DisplayName("Добавление комментария от невалидного пользователя")
    @Test
    void testAddCommentNoAccessError() {
        UserDto user = UserDto.builder().id(2L).build();

        Item item = Item.builder().id(itemId).name("Test Item").owner(UserDtoMapper.mapToUser(user)).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NoAccessAddCommentException.class, () -> itemService.addComment(requestComment, itemId, userId));
    }
}
