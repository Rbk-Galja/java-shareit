package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder
public class ItemDtoBooking {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}

