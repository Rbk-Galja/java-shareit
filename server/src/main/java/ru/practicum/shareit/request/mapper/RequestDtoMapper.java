package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestListAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestDtoMapper {

    public static ItemRequest mapToRequestAdd(NewRequest request, User requestor) {
        return ItemRequest.builder()
                .description(request.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(UserDtoMapper.mapToUserDto(itemRequest.getRequestor()))
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestListAnswerDto mapToRequestWithList(ItemRequest request,
                                                                List<ItemDto> answers) {
        return ItemRequestListAnswerDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requesterName(request.getRequestor().getName())
                .created(request.getCreated())
                .items(answers)
                .build();
    }

}
