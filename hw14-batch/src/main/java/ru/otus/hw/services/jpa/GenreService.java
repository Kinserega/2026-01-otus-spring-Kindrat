package ru.otus.hw.services.jpa;

import ru.otus.hw.dto.jpa.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();
}
