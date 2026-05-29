package ru.otus.hw.services.jpa;

import ru.otus.hw.dto.jpa.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();
}
