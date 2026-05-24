package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

import java.util.Set;

public interface BookRepositoryCustom {

    Flux<Book> findAllWithRelations();

    Mono<Book> findByIdWithRelations(long id);

    Mono<Book> saveBookWithGenres(Book book, Set<Long> genreIds);

    Mono<Book> updateBookWithGenres(Book book, Set<Long> genreIds);

}
