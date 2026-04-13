package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

import java.util.List;

@Component
public class AuthorMapper {

    public AuthorDto toDto(Author author) {
        return new AuthorDto(
                author.getId(),
                author.getFullName()
        );
    }

    public List<AuthorDto> toDtoList(List<Author> authors) {
        return authors.stream()
                .map(this::toDto)
                .toList();
    }
}
