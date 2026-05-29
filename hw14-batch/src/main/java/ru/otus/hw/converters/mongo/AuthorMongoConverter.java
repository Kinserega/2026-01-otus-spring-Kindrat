package ru.otus.hw.converters.mongo;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.AuthorDocument;

@Component
public class AuthorMongoConverter {
    public String authorToString(AuthorDocument authorDocument) {
        return "Id: %s, FullName: %s".formatted(authorDocument.getId(), authorDocument.getFullName());
    }
}
