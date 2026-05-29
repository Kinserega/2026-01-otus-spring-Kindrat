package ru.otus.hw.mapper.jpa;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.jpa.AuthorDto;
import ru.otus.hw.models.jpa.Author;

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
