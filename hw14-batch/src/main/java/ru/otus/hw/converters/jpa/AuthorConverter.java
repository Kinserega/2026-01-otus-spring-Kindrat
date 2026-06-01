package ru.otus.hw.converters.jpa;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.jpa.AuthorDto;

@Component
public class AuthorConverter {
    public String authorToString(AuthorDto author) {
        return "Id: %d, FullName: %s".formatted(author.id(), author.fullName());
    }
}
