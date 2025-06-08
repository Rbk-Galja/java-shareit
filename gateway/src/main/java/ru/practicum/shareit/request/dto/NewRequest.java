package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class NewRequest {
    @NotBlank
    String description;

    public NewRequest() {
    }

    @Builder
    public NewRequest(String description) {
        this.description = description;
    }
}
