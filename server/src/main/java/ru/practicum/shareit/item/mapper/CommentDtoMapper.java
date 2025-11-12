package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentDtoMapper {

    public static Comment mapToCommentAdd(NewCommentRequest request, Item item, User author) {
        return Comment.builder()
                .text(request.getText())
                .created(Instant.now())
                .item(item)
                .author(author)
                .build();
    }

    public static CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .item(ItemDtoMapper.mapToDto(comment.getItem()))
                .created(comment.getCreated())
                .build();
    }
}
