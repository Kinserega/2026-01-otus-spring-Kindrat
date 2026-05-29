package ru.otus.hw.converters.mongo;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.GenreDocument;

@Component
public class GenreMongoConverter {
    public String genreToString(GenreDocument genreDocument) {
        return "Id: %s, Name: %s".formatted(genreDocument.getId(), genreDocument.getName());
    }
}
