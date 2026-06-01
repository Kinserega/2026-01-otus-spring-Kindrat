package ru.otus.hw.services.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.mongo.BookDocument;
import ru.otus.hw.repositories.mongo.AuthorMongoRepository;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookMongoServiceImpl implements BookMongoService {
    private final AuthorMongoRepository authorMongoRepository;

    private final GenreMongoRepository genreMongoRepository;

    private final BookMongoRepository bookMongoRepository;

    private final CommentMongoRepository commentMongoRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDocument> findById(String id) {
        return bookMongoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDocument> findAll() {
        return bookMongoRepository.findAll();
    }

    @Override
    @Transactional
    public BookDocument insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public BookDocument update(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentMongoRepository.deleteByBookDocumentId(id);
        bookMongoRepository.deleteById(id);
    }

    private BookDocument save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null or empty");
        }

        var author = authorMongoRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreMongoRepository.findAllByIdIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new BookDocument(id, title, author, genres);
        return bookMongoRepository.save(book);
    }
}
