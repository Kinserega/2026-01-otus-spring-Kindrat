package ru.otus.hw.services;

import ru.otus.hw.entity.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();
}
