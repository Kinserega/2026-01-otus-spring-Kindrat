package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public record BookUpdateDto(
        @NotNull
        Long id,

        @NotBlank(message = "Book title can't be null")
        @Size(min = 1, max = 100, message = "Book title should be with size from 1 to 100 symbols")
        String title,

        @NotNull(message = "Author id is can't be null")
        Long authorId,

        @NotNull(message = "Genre id is can't be null")
        Set<Long> genreIds

) {

    public BookUpdateDto {
        genreIds = genreIds == null ? new HashSet<>() : new HashSet<>(genreIds);
    }

    public static BookUpdateDto fromBookDto(BookDto bookDto) {
        Set<Long> genreIds = bookDto.genres() == null
                ? Set.of()
                : bookDto.genres().stream()
                .map(GenreDto::id)
                .collect(java.util.stream.Collectors.toSet());

        return new BookUpdateDto(
                bookDto.id(),
                bookDto.title(),
                bookDto.author() == null ? null : bookDto.author().id(),
                genreIds
        );
    }

}
