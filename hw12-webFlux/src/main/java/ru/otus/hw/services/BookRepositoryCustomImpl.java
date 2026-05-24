package ru.otus.hw.services;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private static final String FIND_ALL_BOOKS_WITH_RELATIONS_QUERY = """
            select
                b.id as book_id,
                b.title as book_title,
                a.id as author_id,
                a.full_name as author_full_name,
                g.id as genre_id,
                g.name as genre_name
            from books b
            join authors a on a.id = b.author_id
            left join books_genres bg on bg.book_id = b.id
            left join genres g on g.id = bg.genre_id
            order by b.id, g.id
            """;

    private static final String FIND_BOOK_BY_ID_WITH_RELATIONS_QUERY = """
            select
                b.id as book_id,
                b.title as book_title,
                a.id as author_id,
                a.full_name as author_full_name,
                g.id as genre_id,
                g.name as genre_name
            from books b
            join authors a on a.id = b.author_id
            left join books_genres bg on bg.book_id = b.id
            left join genres g on g.id = bg.genre_id
            where b.id = :bookId
            order by g.id
            """;

    private final DatabaseClient databaseClient;

    @Override
    public Flux<Book> findAllWithRelations() {
        return databaseClient.sql(FIND_ALL_BOOKS_WITH_RELATIONS_QUERY)
                .map(this::mapBookRow)
                .all()
                .collectMultimap(BookRow::bookId)
                .flatMapMany(groupedRows -> Flux.fromIterable(groupedRows.values()))
                .map(this::buildBook);
    }

    @Override
    public Mono<Book> findByIdWithRelations(long id) {
        return databaseClient.sql(FIND_BOOK_BY_ID_WITH_RELATIONS_QUERY)
                .bind("bookId", id)
                .map(this::mapBookRow)
                .all()
                .collectList()
                .filter(rows -> !rows.isEmpty())
                .map(this::buildBook);
    }

    @Override
    public Mono<Book> saveBookWithGenres(Book book, Set<Long> genreIds) {
        return databaseClient.sql("""
                        insert into books(title, author_id)
                        values (:title, :authorId)
                        """)
                .bind("title", book.getTitle())
                .bind("authorId", book.getAuthor().getId())
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .flatMap(savedBookId -> addGenresToBook(savedBookId, genreIds)
                        .then(findByIdWithRelations(savedBookId)));
    }

    @Override
    public Mono<Book> updateBookWithGenres(Book book, Set<Long> genreIds) {
        return databaseClient.sql("""
                        update books
                        set title = :title,
                            author_id = :authorId
                        where id = :bookId
                        """)
                .bind("bookId", book.getId())
                .bind("title", book.getTitle())
                .bind("authorId", book.getAuthor().getId())
                .then()
                .then(removeGenresFromBook(book.getId()))
                .then(addGenresToBook(book.getId(), genreIds))
                .then(findByIdWithRelations(book.getId()));
    }

    private Mono<Void> removeGenresFromBook(Long bookId) {
        return databaseClient.sql("""
                        delete from books_genres
                        where book_id = :bookId
                        """)
                .bind("bookId", bookId)
                .then();
    }

    private Mono<Void> addGenresToBook(Long bookId, Set<Long> genreIds) {
        return Flux.fromIterable(genreIds)
                .flatMap(genreId -> addGenreToBook(bookId, genreId))
                .then();
    }

    private Mono<Void> addGenreToBook(Long bookId, Long genreId) {
        return databaseClient.sql("""
                        insert into books_genres(book_id, genre_id)
                        values (:bookId, :genreId)
                        """)
                .bind("bookId", bookId)
                .bind("genreId", genreId)
                .then();
    }

    private BookRow mapBookRow(Row row, RowMetadata metadata) {
        return new BookRow(
                row.get("book_id", Long.class),
                row.get("book_title", String.class),
                row.get("author_id", Long.class),
                row.get("author_full_name", String.class),
                row.get("genre_id", Long.class),
                row.get("genre_name", String.class)
        );
    }

    private Book buildBook(Collection<BookRow> rows) {
        BookRow firstRow = rows.iterator().next();

        Author author = new Author(
                firstRow.authorId(),
                firstRow.authorFullName()
        );

        List<Genre> genres = rows.stream()
                .filter(row -> row.genreId() != null)
                .map(row -> new Genre(
                        row.genreId(),
                        row.genreName()
                ))
                .toList();

        return new Book(
                firstRow.bookId(),
                firstRow.bookTitle(),
                author,
                genres
        );
    }

    private record BookRow(
            Long bookId,
            String bookTitle,
            Long authorId,
            String authorFullName,
            Long genreId,
            String genreName
    ) {
    }
}