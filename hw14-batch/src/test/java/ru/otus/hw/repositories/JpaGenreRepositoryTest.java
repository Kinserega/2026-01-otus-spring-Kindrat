package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.jpa.GenreRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами")
@DataJpaTest
class JpaGenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("return all genres")
    void shouldReturnAllGenres() {
        Genre firstGenre = new Genre();
        firstGenre.setName("Genre A");

        Genre secondGenre = new Genre();
        secondGenre.setName("Genre B");

        testEntityManager.persist(firstGenre);
        testEntityManager.persist(secondGenre);
        testEntityManager.flush();
        testEntityManager.clear();

        List<Genre> genres = genreRepository.findAll();
        assertThat(genres).extracting(Genre::getName).contains("Genre A", "Genre B");
    }

    @Test
    @DisplayName("find genre by id")
    void shouldFindGenreById() {
        Genre genre = new Genre();
        genre.setName("Single Genre");
        Genre persistedGenre = testEntityManager.persistFlushFind(genre);

        var actualGenre = genreRepository.findById(persistedGenre.getId());

        assertThat(actualGenre).isPresent().get().usingRecursiveComparison().isEqualTo(persistedGenre);
    }

    @Test
    @DisplayName("return genres by ids")
    void shouldReturnGenresByIds() {
        Genre firstGenre = new Genre();
        firstGenre.setName("Genre 1");

        Genre secondGenre = new Genre();
        secondGenre.setName("Genre 2");

        Genre thirdGenre = new Genre();
        thirdGenre.setName("Genre 3");

        Genre persistedFirstGenre = testEntityManager.persist(firstGenre);
        Genre persistedSecondGenre = testEntityManager.persist(secondGenre);
        testEntityManager.persist(thirdGenre);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Genre> genres = genreRepository.findAllByIdIn(
                Set.of(persistedFirstGenre.getId(), persistedSecondGenre.getId())
        );

        assertThat(genres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(persistedFirstGenre.getId(), persistedSecondGenre.getId());
    }
}