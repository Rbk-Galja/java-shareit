package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Builder
    public Comment(Long id, String text, Item item, User author, Instant created) {
        this.id = id;
        this.text = text;
        this.item = item;
        this.author = author;
        this.created = created;
    }

    public Comment() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "text")
    @NotBlank
    String text;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "item_id")
    Item item;

    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;

    @Column(name = "created")
    Instant created;

}
