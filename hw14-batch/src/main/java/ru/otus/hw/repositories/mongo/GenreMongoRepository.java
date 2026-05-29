package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.GenreDocument;

import java.util.List;
import java.util.Set;

public interface GenreMongoRepository extends MongoRepository<GenreDocument, String> {
    List<GenreDocument> findAllByIdIn (Set<String> id);
}
