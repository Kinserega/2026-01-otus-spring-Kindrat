package ru.otus.hw.repositories;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookReadRepository {

    private static final String FIND_ALL_BOOKS = """
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

    private static final String FIND_BOOK_BY_ID = """
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


    public Flux<BookDto> findAll() {
        return databaseClient.sql(FIND_ALL_BOOKS)
                .map(this::toRow)
                .all()
                .collectMultimap(BookRow::bookId)
                .flatMapMany(rowsByBookId -> Flux.fromIterable(rowsByBookId.values()))
                .map(this::toBookDto);
    }

    public Mono<BookDto> findById(Long bookId) {
        return databaseClient.sql(FIND_BOOK_BY_ID)
                .bind("bookId", bookId)
                .map(this::toRow)
                .all()
                .collectList()
                .filter(rows -> !rows.isEmpty())
                .map(this::toBookDto);
    }

    private BookRow toRow(Row row, RowMetadata metadata) {
        return new BookRow(
                row.get("book_id", Long.class),
                row.get("book_title", String.class),
                row.get("author_id", Long.class),
                row.get("author_full_name", String.class),
                row.get("genre_id", Long.class),
                row.get("genre_name", String.class)
        );
    }

    private BookDto toBookDto(Collection<BookRow> rows) {
        BookRow firstRow = rows.iterator().next();

        AuthorDto author = new AuthorDto(
                firstRow.authorId(),
                firstRow.authorFullName()
        );

        List<GenreDto> genres = rows.stream()
                .filter(row -> row.genreId() != null)
                .map(row -> new GenreDto(row.genreId(), row.genreName()))
                .toList();

        return new BookDto(
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