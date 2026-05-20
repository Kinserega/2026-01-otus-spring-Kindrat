package ru.otus.hw.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public BookUpdateDto mapToUpdateDto(BookDto bookDto) {

        Set<Long> genreIds = bookDto.genres() == null ? Set.of() : bookDto.genres().stream()
                .map(GenreDto::id)
                .collect(Collectors.toSet());

        return new BookUpdateDto(bookDto.id(), bookDto.title(),
                bookDto.author() == null ? null : bookDto.author().id(), genreIds);
    }
}
