package ru.practicum.shareit.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "request")
@Data
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "description", nullable = false)
    @NotBlank
    String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    User requestor;
}
