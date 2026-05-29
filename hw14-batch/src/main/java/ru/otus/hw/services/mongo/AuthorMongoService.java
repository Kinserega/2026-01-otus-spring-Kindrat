package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.AuthorDocument;

import java.util.List;

public interface AuthorMongoService {
    List<AuthorDocument> findAll();
}
