package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookReadRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookReadRepository bookReadRepository;


    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return bookReadRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BookDto> findById(long id) {
        return bookReadRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id %d not found".formatted(id)
                )));
    }

    @Override
    @Transactional
    public Mono<BookDto> insert(BookCreateDto dto) {
        return validateReferences(dto.authorId(), dto.genreIds())
                .then(bookRepository.save(new Book(null, dto.title(), dto.authorId())))
                .flatMap(savedBook -> saveGenreLinks(savedBook.getId(), dto.genreIds())
                        .then(findById(savedBook.getId())));
    }

    @Override
    @Transactional
    public Mono<BookDto> update(long id, BookUpdateDto dto) {
        return validateBookExists(id)
                .then(validateReferences(dto.authorId(), dto.genreIds()))
                .then(bookRepository.save(new Book(id, dto.title(), dto.authorId())))
                .flatMap(savedBook -> bookRepository.deleteGenreLinksByBookId(savedBook.getId())
                        .then(saveGenreLinks(savedBook.getId(), dto.genreIds()))
                        .then(findById(savedBook.getId())));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return validateBookExists(id)
                .then(bookRepository.deleteGenreLinksByBookId(id))
                .then(bookRepository.deleteById(id));
    }

    private Mono<Void> validateBookExists(long id) {
        return bookRepository.existsById(id)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id %d not found".formatted(id)
                )))
                .then();
    }

    private Mono<Void> validateReferences(Long authorId, Set<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Genre ids must not be null or empty"));
        }

        return validateAuthorExists(authorId)
                .then(validateGenresExist(genreIds));
    }

    private Mono<Void> validateAuthorExists(Long authorId) {
        return authorRepository.existsById(authorId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Author with id %d not found".formatted(authorId)
                )))
                .then();
    }

    private Mono<Void> validateGenresExist(Set<Long> genreIds) {
        return genreRepository.findAllByIdIn(genreIds)
                .count()
                .filter(foundCount -> foundCount == genreIds.size())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "One or all genres with ids %s not found".formatted(genreIds)
                )))
                .then();
    }

    private Mono<Void> saveGenreLinks(Long bookId, Set<Long> genreIds) {
        return Flux.fromIterable(genreIds)
                .flatMap(genreId -> bookRepository.addGenreLink(bookId, genreId))
                .then();
    }
}
