package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessAddCommentException;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserService userService;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDtoBooking getById(long itemId) {
        log.info("Начинаем получение предмета с id {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        List<Booking> bookings = bookingRepository.findByItem_idAndStatus(itemId, Status.WAITING);
        List<Comment> comments = commentRepository.findByItem_id(itemId);
        ItemDtoBooking itemDtoBooking = ItemDtoMapper.mapToItemDtoBooking(item,
                bookings,
                comments);
        log.info("Получение предмета {} завершено", itemDtoBooking);
        return itemDtoBooking;
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        log.info("Получаем все предметы для пользователя id = {}", userId);
        return itemRepository.findByOwner(UserDtoMapper.mapToUser(userService.getById(userId))).stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto add(long userId, NewItemRequest request) {
        log.info("Начинаем создание предмета {}", request);
        User owner = UserDtoMapper.mapToUser(userService.getById(userId));
        log.info("Определен владелец предмета {}: {}", request, owner);
        ItemRequest itemRequest;
        if (request.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(request.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            log.info("Получаем запрос на создание предмета {}", itemRequest);
        } else {
            itemRequest = null;
        }
        Item item = ItemDtoMapper.mapToItemAdd(request, owner, itemRequest);
        itemRepository.save(item);
        log.info("Создание предмета {} прошло успешно, предмету присвоен id = {}", item, item.getId());
        return ItemDtoMapper.mapToDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long idItem, UpdateItemRequest request) {
        log.info("Началось обновление вещи id = {}", idItem);
        Item oldItem = itemRepository.findById(idItem).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (userId == oldItem.getOwner().getId()) {
            Item updateItem = ItemDtoMapper.mapToDtoUpdate(oldItem, request);
            updateItem = itemRepository.save(updateItem);
            log.info("Обновление предмета {} завершено", updateItem);
            return ItemDtoMapper.mapToDto(updateItem);
        }
        log.error("У пользователя id = {} нет доступа к вещи id = {}", userId, idItem);
        throw new NoAccessException("Отказано в доступе к предмету");
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> search = new ArrayList<>();
        if (text.isEmpty() || text.isBlank()) {
            log.info("Введен пустой поисковый запрос");
            return search;
        }
        log.info("Возвращаем список вещей по запросу {}", text);
        search = itemRepository.searchItem(text).stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
        return search;
    }

    @Override
    @Transactional
    public CommentDto addComment(NewCommentRequest request, long itemId, long userId) {
        log.info("Начинаем добавление комментария {}", request);
        User author = UserDtoMapper.mapToUser(userService.getById(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        Booking booking = bookingRepository.findByItemAndBooker(item, author);
        log.info("Получено бронирование {} для пользователя id = {}", booking, userId);
        if (booking == null) {
            log.error("Бронирование для предмета id = {} от пользователя id = {} не найдено", itemId, userId);
            throw new NoAccessAddCommentException("Бронирование для предмета не найдено, невозможно добавить комментарий");
        }
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            log.error("Срок бронирования предмета id = {} для пользователя id = {} еще не закончился", itemId, userId);
            throw new NoAccessAddCommentException("Срок бронирования предмета еще не закончился");
        }

        Comment comment = CommentDtoMapper.mapToCommentAdd(request, item, author);
        commentRepository.save(comment);
        log.info("Создание комментария прошло успешно {}, комментарию присвоен id = {}", comment, comment.getText());
        return CommentDtoMapper.mapToDto(comment);
    }
}
