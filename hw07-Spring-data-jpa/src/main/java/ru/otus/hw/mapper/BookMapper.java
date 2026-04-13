package ru.otus.hw.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    public BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                authorMapper.toDto(book.getAuthor()),
                genreMapper.toDtoList(book.getGenres())
        );
    }

    public List<BookDto> toDtoList(List<Book> books) {
        return books.stream()
                .map(this::toDto)
                .toList();
    }

    private List<String> mapGenres(Book book) {
        return book.getGenres().stream()
                .map(Genre::getName)
                .toList();
    }

}
