package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.validator.UpdateValidate;

@Entity
@Table(name = "users")
@Data
public class User {

    @Builder
    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(groups = {UpdateValidate.class})
    long id;

    @Column(name = "name")
    String name;

    @Column(name = "email", nullable = false)
    @Email
    String email;

}
