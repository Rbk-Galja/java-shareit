package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validator.UpdateValidate;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    @NotNull(groups = {UpdateValidate.class})
    long id;

    String name;
    String email;

}
