package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record BookCreateDto(

        @NotBlank(message = "Book title can't be null")
        @Size(min = 1, max = 100, message = "Book title should be with size from 1 to 100 symbols")
        String title,

        @NotNull(message = "Author id is can't be null")
        Long authorId,

        @NotEmpty(message = "At least one genre should be selected")
        Set<Long> genreIds
) {}
