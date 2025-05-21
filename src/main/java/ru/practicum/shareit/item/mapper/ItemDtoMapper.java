package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserDtoMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemDtoMapper {

    public static Item mapToItemAdd(NewItemRequest request, User owner) {
        return Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .available(request.getAvailable())
                .owner(owner)
                .request(request.getRequest())
                .build();
    }

    public static ItemDto mapToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(UserDtoMapper.mapToUserDto(item.getOwner()))
                .build();
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
