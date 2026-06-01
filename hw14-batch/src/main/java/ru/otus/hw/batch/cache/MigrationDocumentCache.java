package ru.otus.hw.batch.cache;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.AuthorDocument;
import ru.otus.hw.models.mongo.BookDocument;
import ru.otus.hw.models.mongo.GenreDocument;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MigrationDocumentCache {

    private final Map<Long, AuthorDocument> authors = new ConcurrentHashMap<>();

    private final Map<Long, GenreDocument> genres = new ConcurrentHashMap<>();

    private final Map<Long, BookDocument> books = new ConcurrentHashMap<>();

    public AuthorDocument getAuthor(Long sourceId) {
        return authors.get(sourceId);
    }

    public void putAuthor(Long sourceId, AuthorDocument authorDocument) {
        authors.put(sourceId, authorDocument);
    }

    public List<GenreDocument> getGenres(List<Long> sourceIds) {
        return sourceIds.stream()
                .map(genres::get)
                .toList();
    }

    public void putGenre(Long sourceId, GenreDocument genreDocument) {
        genres.put(sourceId, genreDocument);
    }

    public BookDocument getBook(Long sourceId) {
        return books.get(sourceId);
    }

    public void putBook(Long sourceId, BookDocument bookDocument) {
        books.put(sourceId, bookDocument);
    }

    public void clear() {
        authors.clear();
        genres.clear();
        books.clear();
    }

}
