package ru.otus.hw.converters.jpa;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.jpa.GenreDto;


@Component
public class GenreConverter {
    public String genreToString(GenreDto genre) {
        return "Id: %d, Name: %s".formatted(genre.id(), genre.name());
    }
}
