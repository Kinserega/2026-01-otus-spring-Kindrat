package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(GenreServiceImpl.class)
class GenreServiceIntegrationTest extends AbstractMongoServiceTest {

    @Autowired
    private GenreService genreService;

    @Test
    @DisplayName("должен возвращать все жанры")
    void shouldFindAllGenres() {
        var genres = genreService.findAll();
        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre A", "Genre B");
    }
}