package ru.otus.hw.converters.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.BookDocument;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookMongoConverter {
    private final AuthorMongoConverter authorMongoConverter;

    private final GenreMongoConverter genreMongoConverter;

    public String bookToString(BookDocument bookDocument) {
        var genresString = bookDocument.getGenreDocuments().stream()
                .map(genreMongoConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                bookDocument.getId(),
                bookDocument.getTitle(),
                authorMongoConverter.authorToString(bookDocument.getAuthorDocument()),
                genresString);
    }
}
