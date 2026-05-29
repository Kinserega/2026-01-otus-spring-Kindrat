package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.BookDocument;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookMongoService {
    Optional<BookDocument> findById(String id);

    List<BookDocument> findAll();

    BookDocument insert(String title, String authorId, Set<String> genresIds);

    BookDocument update(String id, String title, String authorId, Set<String> genresIds);

    void deleteById(String id);
}
