package ru.otus.hw.mapper.jpa;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.jpa.CommentDto;
import ru.otus.hw.models.jpa.Comment;

import java.util.List;


@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText()
        );
    }

    public List<CommentDto> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toDto)
                .toList();
    }

}
