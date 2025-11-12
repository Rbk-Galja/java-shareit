package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListAnswerDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.mapper.RequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImpl implements ItemRequestService {
    ItemRequestRepository itemRequestRepository;
    UserService userService;
    ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto add(long userId, NewRequest request) {
        log.info("Начинаем создание запроса {}", request);
        User requestor = UserDtoMapper.mapToUser(userService.getById(userId));
        log.info("Определен владелец запроса {}: {}", request, requestor);
        ItemRequest itemRequest = RequestDtoMapper.mapToRequestAdd(request, requestor);
        ItemRequest result = itemRequestRepository.save(itemRequest);
        log.info("Создание запроса {} прошло успешно, запросу присвоен id = {}", result, result.getId());
        return RequestDtoMapper.mapToDto(result);
    }

    @Override
    @Transactional
    public List<ItemRequestListAnswerDto> findByRegistorId(long requestorId) {
        log.info("Начинаем получение всех запросов пользователя id = {} со списком ответов", requestorId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(requestorId);
        List<ItemRequestListAnswerDto> listAnswers = requests.stream()
                .map(request -> RequestDtoMapper.mapToRequestWithList(request,
                        mapAnswers(itemRepository.findByRequestId(request.getId()))))
                .toList();
        log.info("Получен список всех запросов для пользователя id = {}: {}", requestorId, listAnswers);
        return listAnswers;
    }

    @Override
    public List<ItemRequestDto> findAll() {
        log.info("Получаем список всех запросов от всех пользователей");
        return itemRequestRepository.findAll().stream()
                .map(RequestDtoMapper::mapToDto)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), list -> {
                            Collections.reverse(list);
                            return list.stream();
                        }))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemRequestListAnswerDto findById(long requestId) {
        log.info("Начинаем получение запроса id = {}", requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        log.info("Получаем лист ответов для запроса {}", itemRequest);
        List<ItemDto> answers = itemRepository.findByRequestId(requestId).stream().map(ItemDtoMapper::mapToDto).toList();
        ItemRequestListAnswerDto request = RequestDtoMapper.mapToRequestWithList(itemRequest, answers);
        log.info("Получение запроса завершено {}", request);
        return request;
    }

    private List<ItemDto> mapAnswers(List<Item> items) {
        return items.stream().map(ItemDtoMapper::mapToDto).toList();
    }
}
