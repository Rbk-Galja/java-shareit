package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class NewCommentRequest {
    @NotBlank
    private String text;

    public NewCommentRequest() {
    }

    @Builder
    public NewCommentRequest(String text) {
        this.text = text;
    }
}
