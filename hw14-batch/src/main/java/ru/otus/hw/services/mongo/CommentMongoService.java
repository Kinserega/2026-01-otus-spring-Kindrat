package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.CommentDocument;

import java.util.List;
import java.util.Optional;

public interface CommentMongoService {

    Optional<CommentDocument> findById(String id);

    List<CommentDocument> findAllByBookId(String bookId);

    CommentDocument insert(String text, String bookId);

    CommentDocument update(String id, String text, String bookId);

    void deleteById(String id);
}
