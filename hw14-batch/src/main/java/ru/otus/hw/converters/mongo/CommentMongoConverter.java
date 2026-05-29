package ru.otus.hw.converters.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.CommentDocument;

@RequiredArgsConstructor
@Component
public class CommentMongoConverter {

    private final BookMongoConverter bookMongoConverter;

    public String commentToString(CommentDocument commentDocument) {
        return "Id: %s, text: %s, book: {%s}".formatted(
                commentDocument.getId(),
                commentDocument.getText(),
                bookMongoConverter.bookToString(commentDocument.getBookDocument())
        );
    }
}
