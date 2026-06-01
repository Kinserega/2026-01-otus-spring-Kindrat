package ru.otus.hw.converters.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.jpa.CommentDto;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    private final BookConverter bookConverter;

    public String commentToString(CommentDto comment) {
        return "Id: %d, Text: %s".formatted(
                comment.id(),
                comment.text());
    }
}
