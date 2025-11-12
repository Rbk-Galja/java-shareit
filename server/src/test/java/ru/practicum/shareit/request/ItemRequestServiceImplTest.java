package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestListAnswerDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.mapper.RequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    UserService userService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    RequestDtoMapper requestDtoMapper;
    @Mock
    ItemRepository itemRepository;

    final long userId = 1L;
    final long requestId = 1L;
    final NewRequest request = NewRequest.builder()
            .description("Test request")
            .build();

    final User requestor = User.builder()
            .id(userId)
            .name("Test User")
            .email("test@email.com")
            .build();

    final UserDto requestorDto = UserDto.builder()
            .id(userId)
            .name("Test User")
            .email("test@email.com")
            .build();

    final ItemRequest itemRequest = ItemRequest.builder()
            .id(requestId)
            .description("Test request")
            .requestor(requestor)
            .build();

    final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(requestId)
            .description("Test request")
            .requestor(requestorDto)
            .build();

    @DisplayName("Добавление валдиного запроса")
    @Test
    void testAddSuccess() {
        when(userService.getById(userId)).thenReturn(requestorDto);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.add(userId, request);

        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getRequestor(), result.getRequestor());

        verify(userService).getById(userId);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @DisplayName("Добавление запроса от несуществующего пользователя")
    @Test
    void testAddUserNotFound() {
        NewRequest request = NewRequest.builder()
                .description("Test request")
                .build();

        when(userService.getById(userId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> itemRequestService.add(userId, request));
    }

    @DisplayName("Создание запроса с пустым описанием")
    @Test
    void testAddEmptyDescription() {
        NewRequest request = NewRequest.builder()
                .description("")
                .build();

        assertThrows(NullPointerException.class, () -> itemRequestService.add(userId, request));
    }

    @DisplayName("Получение всех запросов их создателем")
    @Test
    void testFindByRegistorIdSuccess() {
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Request 2")
                .requestor(requestor)
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .request(itemRequest)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .request(itemRequest2)
                .build();

        ItemRequestListAnswerDto dto1 = ItemRequestListAnswerDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .items(List.of(ItemDto.builder().id(item1.getId()).name(item1.getName()).build()))
                .build();

        ItemRequestListAnswerDto dto2 = ItemRequestListAnswerDto.builder()
                .id(itemRequest2.getId())
                .description(itemRequest2.getDescription())
                .items(List.of(ItemDto.builder().id(item2.getId()).name(item2.getName()).build()))
                .build();

        when(itemRequestRepository.findByRequestorId(userId))
                .thenReturn(List.of(itemRequest, itemRequest2));

        when(itemRepository.findByRequestId(itemRequest.getId()))
                .thenReturn(List.of(item1));

        when(itemRepository.findByRequestId(itemRequest2.getId()))
                .thenReturn(List.of(item2));

        List<ItemRequestListAnswerDto> result = itemRequestService.findByRegistorId(userId);

        assertEquals(2, result.size());
        assertEquals(dto1.getId(), result.get(0).getId());
        assertEquals(dto1.getDescription(), result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());

        assertEquals(dto2.getId(), result.get(1).getId());
        assertEquals(dto2.getDescription(), result.get(1).getDescription());
        assertEquals(1, result.get(1).getItems().size());

        verify(itemRequestRepository).findByRequestorId(userId);
        verify(itemRepository).findByRequestId(itemRequest.getId());
        verify(itemRepository).findByRequestId(itemRequest2.getId());
    }

    @DisplayName("Получение пустого списка запросов пользователя")
    @Test
    void testFindByRegistorIdNoRequests() {
        when(itemRequestRepository.findByRequestorId(userId)).thenReturn(List.of());

        List<ItemRequestListAnswerDto> result = itemRequestService.findByRegistorId(userId);

        assertTrue(result.isEmpty());

        verify(itemRequestRepository).findByRequestorId(userId);
    }

    @DisplayName("Получение всех запросов от всех пользователей")
    @Test
    void testFindAllRequestForAllUsers() {
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Request 2")
                .requestor(requestor)
                .build();

        when(itemRequestRepository.findAll()).thenReturn(List.of(itemRequest, itemRequest2));

        List<ItemRequestDto> result = itemRequestService.findAll();

        assertEquals(2, result.size());

        verify(itemRequestRepository).findAll();
    }

    @DisplayName("Получение запроса по id")
    @Test
    void testGetRequestById() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .request(itemRequest)
                .build();

        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(userId)).thenReturn(List.of(item1));

        ItemRequestListAnswerDto result = itemRequestService.findById(requestId);

        assertEquals(requestId, result.getId());
        verify(itemRepository).findByRequestId(requestId);
    }

}


