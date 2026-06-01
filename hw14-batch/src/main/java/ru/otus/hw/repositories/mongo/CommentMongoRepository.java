package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.CommentDocument;

import java.util.List;

public interface CommentMongoRepository extends MongoRepository<CommentDocument, String> {
    List<CommentDocument> findAllByBookDocumentId(String bookId);

    void deleteByBookDocumentId(String id);
}