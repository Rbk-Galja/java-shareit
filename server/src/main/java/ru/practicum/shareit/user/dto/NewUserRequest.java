package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "Поле имя пустое")
    private String name;

    @NotBlank(message = "Поле email пустое")
    @Email(message = "Указан некорректный формат email")
    private String email;

    public NewUserRequest() {
    }

    @Builder
    public NewUserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
