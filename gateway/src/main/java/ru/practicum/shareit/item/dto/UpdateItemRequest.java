package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateItemRequest {
    @NotBlank(message = "название не должно быть пустым")
    private String name;

    @NotBlank(message = "описание не должно быть пустым")
    private String description;

    @NotNull(message = "не установлен статус доступности для бронирования")
    private Boolean available;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return available != null;
    }
}
