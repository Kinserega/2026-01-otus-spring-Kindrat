package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Book {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private final Long id;

    @NotNull
    @ToString.Include
    private final String title;

    @NotNull
    private final Author author;

    @NotNull
    private final List<Genre> genres;
}