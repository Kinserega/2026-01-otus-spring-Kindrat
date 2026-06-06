package ru.otus.hw.mapper;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;

import java.util.List;

@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        return new GenreDto(
                genre.getId(),
                genre.getName()
        );
    }

    public List<GenreDto> toDtoList(List<Genre> genres) {
        return genres.stream()
                .map(this::toDto)
                .toList();
    }
}
