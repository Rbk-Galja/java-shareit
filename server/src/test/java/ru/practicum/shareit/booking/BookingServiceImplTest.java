package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotAvailableItemException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImplTest {
    @ExtendWith(MockitoExtension.class)
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;

    final long bookerId = 1L;
    final long itemId = 1L;
    final long bookingId = 1L;
    final long ownerId = 2L;
    final State state = State.ALL;
    final NewBookingRequest request = NewBookingRequest.builder()
            .itemId(itemId)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(1))
            .build();
    final User booker = User.builder()
            .id(bookerId)
            .name("Booker")
            .email("booker@email.com")
            .build();
    final User owner = User.builder()
            .id(ownerId)
            .name("Owner")
            .build();
    final UserDto ownerDto = UserDto.builder()
            .id(ownerId)
            .name("Owner")
            .build();
    final UserDto bookerDto = UserDto.builder()
            .id(bookerId)
            .name("Booker")
            .email("booker@email.com")
            .build();
    final Item item = Item.builder()
            .id(itemId)
            .name("Test Item")
            .owner(owner)
            .available(true)
            .build();
    final ItemDto itemDto = ItemDto.builder()
            .id(itemId)
            .name("Test Item")
            .owner(ownerDto)
            .available(true)
            .build();
    final Booking booking = Booking.builder()
            .id(bookingId)
            .booker(booker)
            .item(item)
            .start(request.getStart())
            .end(request.getEnd())
            .build();
    final Booking booking2 = Booking.builder()
            .id(2L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(2))
            .status(Status.REJECTED)
            .item(item)
            .booker(User.builder().id(bookerId).build())
            .build();
    final BookingDto bookingDto = BookingDto.builder()
            .id(bookingId)
            .booker(bookerDto)
            .item(itemDto)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(1))
            .build();
    final BookingDto bookingDto2 = BookingDto.builder()
            .id(2L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(2))
            .status(Status.APPROVED)
            .build();

    @DisplayName("Создание валидного бронирования")
    @Test
    void testAddBookingSuccess() {
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.add(request, bookerId);

        assertNotNull(result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @DisplayName("Бронирование вещи с available false")
    @Test
    void testAddItemNotAvailable() {
        Item item = Item.builder()
                .id(itemId)
                .name("Test Item")
                .available(false)
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotAvailableItemException.class, () -> bookingService.add(request, bookerId));
    }

    @DisplayName("Бронирование вещи от несуществующего пользователя")
    @Test
    void testAddUserNotFound() {
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.add(request, bookerId));
    }

    @DisplayName("Бронирование несуществующей вещи ")
    @Test
    void testAddItemNotFound() {
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.add(request, bookerId));
    }

    @DisplayName("Одобрение статуса бронирования владельцем")
    @Test
    void testUpdateStatus_Approved_Success() {
        boolean approved = true;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.updateStatus(ownerId, bookingId, approved);

        assertEquals(Status.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @DisplayName("Отклонение бронирования владельцем")
    @Test
    void testUpdateStatusRejectedSuccess() {
        boolean approved = false;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.updateStatus(ownerId, bookingId, approved);

        assertEquals(Status.REJECTED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @DisplayName("Ошибка доступа к статусу бронирования")
    @Test
    void testUpdateStatusNoAccess() {
        long userId = 2L;
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NullPointerException.class, () -> bookingService.updateStatus(userId, bookingId, approved));
    }

    @DisplayName("Бронирование не найдено")
    @Test
    void testUpdateStatusNotFound() {
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.updateStatus(ownerId, bookingId, approved));
    }

    @DisplayName("Получение бронирования по id владельцем вещи")
    @Test
    void testFindByIdSuccessOwner() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findById(ownerId, bookingId);

        assertEquals(bookingId, result.getId());
        verify(bookingRepository).findById(bookingId);
    }

    @DisplayName("Получение бронирования по id забронировавшим пользователем")
    @Test
    void testFindByIdSuccessBooker() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findById(ownerId, bookingId);

        assertEquals(bookingId, result.getId());
        verify(bookingRepository).findById(bookingId);
    }

    @DisplayName("Попытка получить несуществующее бронирование")
    @Test
    void testFindByIdNotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findById(ownerId, bookingId));
    }

    @DisplayName("Попытка получить бронирование посторонним пользователем")
    @Test
    void testFindByIdNoAccess() {
        long userId = 3L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NoAccessException.class, () -> bookingService.findById(userId, bookingId));
    }

    @DisplayName("Получение всех бронирований забронировавшим пользователем")
    @Test
    void testFindByBookerIdSuccess() {
        when(bookingRepository.findByBooker_id(bookerId)).thenReturn(List.of(booking, booking2));

        List<BookingDto> result = bookingService.findByBookerId(bookerId, state);

        assertEquals(2, result.size());
        assertEquals(bookingDto.getId(), result.get(0).getId());
        assertEquals(bookingDto2.getId(), result.get(1).getId());
        verify(bookingRepository).findByBooker_id(bookerId);
    }

    @DisplayName("Получение пустого листа бронирований забронировавшим пользователем")
    @Test
    void testFindByBookerIdNoBookings() {
        when(bookingRepository.findByBooker_id(bookerId)).thenReturn(List.of());

        List<BookingDto> result = bookingService.findByBookerId(bookerId, state);

        assertTrue(result.isEmpty());
        verify(bookingRepository).findByBooker_id(bookerId);
    }

    @DisplayName("Получение всех бронирований с разным статусом для забронировавшего пользователя")
    @Test
    void testFindByBookerIdAllStates() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.REJECTED)
                .build();
        when(bookingRepository.findByBooker_id(bookerId)).thenReturn(List.of(booking, booking2));

        List<BookingDto> result = bookingService.findByBookerId(bookerId, state);

        assertEquals(2, result.size());
        verify(bookingRepository).findByBooker_id(bookerId);
    }

    @DisplayName("Получение всех бронирований владельцем вещи")
    @Test
    void testFindByOwnerIdSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwner_id(anyLong())).thenReturn(List.of(booking, booking2));

        List<BookingDto> result = bookingService.findByOwnerId(ownerId, state);

        assertEquals(2, result.size());
        assertEquals(bookingDto.getId(), result.get(0).getId());
        assertEquals(bookingDto2.getId(), result.get(1).getId());
        verify(bookingRepository).findByItemOwner_id(ownerId);
    }

    @DisplayName("Получение пустого листа бронирований владельцем вещи")
    @Test
    void testFindByOwnerIdNoBookings() {
        when(bookingRepository.findByBooker_id(ownerId)).thenReturn(List.of());

        List<BookingDto> result = bookingService.findByBookerId(ownerId, state);

        assertTrue(result.isEmpty());
        verify(bookingRepository).findByBooker_id(ownerId);
    }

    @DisplayName("Получение всех бронирований с разным статусом для забронировавшего пользователя")
    @Test
    void testFindOwnerIdAllStates() {
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.REJECTED)
                .build();
        when(bookingRepository.findByBooker_id(ownerId)).thenReturn(List.of(booking, booking2));

        List<BookingDto> result = bookingService.findByBookerId(ownerId, state);

        assertEquals(2, result.size());
        verify(bookingRepository).findByBooker_id(ownerId);
    }
}






