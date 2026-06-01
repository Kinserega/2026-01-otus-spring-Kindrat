package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.GenreDocument;

import java.util.List;

public interface GenreMongoService {
    List<GenreDocument> findAll();
}
