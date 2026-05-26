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
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;


    @Override
    @Transactional(readOnly = true)
    public Flux<BookDto> findAll() {
        return bookRepository.findAllWithRelations()
                .map(bookMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BookDto> findById(long id) {
        return bookRepository.findByIdWithRelations(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id %d not found".formatted(id)
                )))
                .map(bookMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> insert(BookCreateDto dto) {
        return save(null, dto.title(), dto.authorId(), dto.genreIds())
                .map(bookMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<BookDto> update(long id, BookUpdateDto dto) {
        return validateBookExists(id)
                .then(save(id, dto.title(), dto.authorId(), dto.genreIds()))
                .map(bookMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return validateBookExists(id)
                .then(bookRepository.deleteById(id));
    }

    private Mono<Book> save(Long id, String title, Long authorId, Set<Long> genreIds) {
        return validateReferences(authorId, genreIds)
                .then(authorRepository.findById(authorId))
                .zipWith(genreRepository.findAllByIdIn(genreIds).collectList())
                .map(tuple -> new Book(id, title, tuple.getT1(), tuple.getT2()))
                .flatMap(book -> id == null
                        ? bookRepository.saveBookWithGenres(book)
                        : bookRepository.updateBookWithGenres(book));
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
        if (authorId == null) {
            return Mono.error(new IllegalArgumentException("Author id must not be null"));
        }
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
}
