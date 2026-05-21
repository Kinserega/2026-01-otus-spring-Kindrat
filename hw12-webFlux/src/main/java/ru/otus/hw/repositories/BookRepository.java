package ru.otus.hw.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

public interface BookRepository extends ReactiveCrudRepository<Book, Long> {

    @Query("delete from books_genres where book_id = :bookId")
    Mono<Void> deleteGenreLinksByBookId(Long bookId);

    @Query("insert into books_genres(book_id, genre_id) values (:bookId, :genreId)")
    Mono<Void> addGenreLink(Long bookId, Long genreId);

}
