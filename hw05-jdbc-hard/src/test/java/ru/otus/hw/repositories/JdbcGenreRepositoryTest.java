package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами")
@JdbcTest
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository repositoryJdbc;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = repositoryJdbc.findAll();
        assertThat(actualGenres).containsExactly(
                        new Genre(1L, "Genre_1"),
                        new Genre(2L, "Genre_2"),
                        new Genre(3L, "Genre_3"),
                        new Genre(4L, "Genre_4"),
                        new Genre(5L, "Genre_5"),
                        new Genre(6L, "Genre_6")
                );
    }

    @DisplayName("должен загружать жанры по списку id")
    @Test
    void shouldReturnCorrectGenresByIds() {
        var actualGenres = repositoryJdbc.findAllByIds(Set.of(1L, 2L));
        assertThat(actualGenres)
                .containsExactly(
                        new Genre(1L, "Genre_1"),
                        new Genre(2L, "Genre_2")
                );
    }

    @DisplayName("должен возвращать пустой список, если передан пустой набор id")
    @Test
    void shouldReturnEmptyListForEmptyIds() {
        var actualGenres = repositoryJdbc.findAllByIds(Set.of());
        assertThat(actualGenres).isEmpty();
    }

    @DisplayName("должен возвращать пустой список, если жанры по id не найдены")
    @Test
    void shouldReturnEmptyListWhenGenresNotFound() {
        var actualGenres = repositoryJdbc.findAllByIds(Set.of(7L, 8L));
        assertThat(actualGenres).isEmpty();
    }

    @DisplayName("должен загружать только существующие жанры по списку id")
    @Test
    void shouldReturnOnlyExistingGenresByIds() {
        var actualGenres = repositoryJdbc.findAllByIds(Set.of(2L, 4L, 7L));
        assertThat(actualGenres)
                .containsExactly(
                        new Genre(2L, "Genre_2"),
                        new Genre(4L, "Genre_4")
                );
    }
}