package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserService userService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto getById(long id) {
        log.info("Начинаем получение предмета с id {}", id);
        Item item = itemRepository.getById(id).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        log.info("Получение предмета {} завершено", item);
        return ItemDtoMapper.mapToDto(item);
    }

    @Override
    public List<ItemDto> getAll() {
        log.info("Начинаем получение всех предметов");
        return itemRepository.getAll().stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemDto add(long userId, NewItemRequest request) {
        log.info("Начинаем создание предмета {}", request);
        User owner = UserDtoMapper.mapToUser(userService.getById(userId));
        log.info("Определен владелец предмета {}: {}", request, owner);
        Item item = ItemDtoMapper.mapToItemAdd(request, owner);
        itemRepository.save(item);
        log.info("Создание предмета {} прошло успешно", item);
        return ItemDtoMapper.mapToDto(item);
    }

    @Override
    public ItemDto update(long userId, long idItem, UpdateItemRequest request) {
        log.info("Началось обновление вещи id = {}", idItem);
        Item oldItem = ItemDtoMapper.mapToItem(getById(idItem));
        if (userId == oldItem.getOwner().getId()) {
            Item updateItem = ItemDtoMapper.mapToDtoUpdate(oldItem, request);
            updateItem = itemRepository.update(idItem, updateItem);
            log.info("Обновление предмета {} завершено", updateItem);
            return ItemDtoMapper.mapToDto(updateItem);
        }
        log.error("У пользователя id = {} нет доступа к вещи id = {}", userId, idItem);
        throw new NoAccessException("Отказано в доступе к предмету");
    }

    @Override
    public List<ItemDto> getUserItems(long userId) {
        return itemRepository.getUserItems(UserDtoMapper.mapToUser(userService.getById(userId))).stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Возвращаем список вещей по запросу {}", text);
        return itemRepository.searchItem(text).stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
    }
}
