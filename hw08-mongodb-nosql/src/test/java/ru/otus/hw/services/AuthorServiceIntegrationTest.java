package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(AuthorServiceImpl.class)
class AuthorServiceIntegrationTest extends AbstractMongoServiceTest {

    @Autowired
    private AuthorService authorService;

    @Test
    @DisplayName("должен возвращать всех авторов")
    void shouldFindAllAuthors() {
        var authors = authorService.findAll();

        assertThat(authors)
                .extracting(Author::getFullName)
                .containsExactlyInAnyOrder("Author A", "Author B");
    }
}