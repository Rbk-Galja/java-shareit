package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "название не должно быть пустым")
    private String name;

    @NotBlank(message = "описание не должно быть пустым")
    private String description;

    @NotNull(message = "не установлен статус доступности для бронирования")
    private Boolean available;

    private Long requestId;
}

