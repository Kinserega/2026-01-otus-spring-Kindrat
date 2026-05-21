package ru.otus.hw.mapper;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {
                AuthorMapper.class,
                GenreMapper.class
        }
)
public interface BookMapper {

    default BookDto toDto(Book book, Author author, List<Genre> genres) {
        if (book == null) {
            return null;
        }
        return new BookDto(
                book.getId(),
                book.getTitle(),
                mapAuthor(author),
                mapGenres(genres)
        );
    }

    AuthorDto mapAuthor(Author author);

    List<GenreDto> mapGenres(List<Genre> genres);
}
