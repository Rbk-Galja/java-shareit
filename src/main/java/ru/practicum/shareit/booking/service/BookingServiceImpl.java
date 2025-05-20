package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotAvailableItemException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ParameterNotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<BookingDto> findAll() {
        log.info("Начинаем получение всех бронирований");
        List<BookingDto> bookings = bookingRepository.findAll().stream()
                .map(BookingDtoMapper::mapToBookingDto)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), list -> {
                            Collections.reverse(list);
                            return list.stream();
                        }))
                .collect(Collectors.toList());
        log.info("Список всех бронирований получен: {}", bookings);
        return bookings;
    }

    @Override
    public BookingDto add(NewBookingRequest request, long bookerId) {
        log.info("Начинаем создание бронирования предмета id = {}", request.getItemId());
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Бронирование от неизвестного пользователя"));
        log.info("Определен владелец бронирования {}", booker);
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет для бронирования не найден"));
        if (item.getAvailable()) {
            Booking booking = BookingDtoMapper.mapToBookingAdd(request, booker, item);
            bookingRepository.save(booking);
            log.info("Создание бронирования прошло успешно");
            return BookingDtoMapper.mapToBookingDto(booking);
        }
        log.error("Бронирование предмета недоступно");
        throw new NotAvailableItemException("Бронирование предмета недоступно");
    }

    @Override
    public BookingDto updateStatus(long userId, long id, boolean approved) {
        log.info("Начинаем обновление статуса бронирования id = {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (userId == booking.getItem().getOwner().getId()) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            log.info("Статус бронирования изменён на {}, обновление статуса бронирования завершено",
                    booking.getStatus());
            return BookingDtoMapper.mapToBookingDto(bookingRepository.save(booking));
        }
        log.error("У пользователя id = {} нет доступа к бронированию id = {}", userId, id);
        throw new NoAccessException("Отказано в доступе к бронированию");
    }

    @Override
    public BookingDto findById(long userId, long bookingId) {
        log.info("Начинаем получение бронирования id = {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        log.info("Бронирование {} получено", booking);
        if (userId == booking.getItem().getOwner().getId() || userId == booking.getBooker().getId()) {
            log.info("Получение бронирования id = {} завершено", bookingId);
            return BookingDtoMapper.mapToBookingDto(booking);
        }
        log.error("У пользователя id = {} нет доступа к бронированию id = {}", userId, bookingId);
        throw new NoAccessException("Отказано в доступе к бронированию");
    }

    @Override
    public List<BookingDto> findByBookerId(long bookerId, String state) {
        log.info("Начинаем получение всех бронирований для пользователя id = {}", bookerId);
        List<BookingDto> bookings = bookingRepository.findByBooker_id(bookerId).stream()
                .map(BookingDtoMapper::mapToBookingDto)
                .toList();
        return checkState(bookings, state);
    }

    @Override
    public List<BookingDto> findByOwnerId(long ownerId, String state) {
        log.info("Начинаем получение всех бронирований для всех вещей пользователя id = {}", ownerId);
        List<BookingDto> bookings = bookingRepository.findByItemOwner_id(ownerId).stream()
                .map(BookingDtoMapper::mapToBookingDto)
                .toList();
        return checkState(bookings, state);
    }

    private List<BookingDto> checkState(List<BookingDto> bookings, String state) {
        return switch (state) {
            case "CURRENT" -> bookings.stream().filter(booking -> booking.getStatus().equals(Status.APPROVED)).toList();
            case "PAST" -> bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).toList();
            case "FUTURE" ->
                    bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).toList();
            case "WAITING" -> bookings.stream().filter(booking -> booking.getStatus().equals(Status.WAITING)).toList();
            case "REJECTED" ->
                    bookings.stream().filter(booking -> booking.getStatus().equals(Status.REJECTED)).toList();
            case "ALL" -> bookings;
            default -> {
                log.error("Некорректный запрос сортировки: {}", state);
                throw new ParameterNotValidException("Введен некорректный запрос");
            }
        };
    }
}
