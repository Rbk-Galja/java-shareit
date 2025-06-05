package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.RequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserDtoMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemDtoMapper {

    public static Item mapToItemAdd(NewItemRequest request, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .owner(owner)
                .request(itemRequest)
                .build();
    }

    public static ItemDto mapToDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(UserDtoMapper.mapToUserDto(item.getOwner()))
                .build();
        if (item.getRequest() != null) {
            itemDto.setItemRequest(RequestDtoMapper.mapToDto(item.getRequest()));
        }
        return itemDto;
    }

    public static Item mapToDtoUpdate(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }

    public static ItemDtoBooking mapToItemDtoBooking(Item item, List<Booking> bookings, List<Comment> comments) {
        ItemDtoBooking itemDtoBooking = ItemDtoBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments.stream().map(CommentDtoMapper::mapToDto).toList())
                .build();
        if (!bookings.isEmpty()) {
            itemDtoBooking.setLastBooking(BookingDtoMapper.mapToBookingDto(bookings.getLast()));
            if (bookings.size() > 1) {
                itemDtoBooking.setNextBooking(BookingDtoMapper.mapToBookingDto(bookings.get(bookings.size() - 2)));
            }
        }
        return itemDtoBooking;
    }
}
