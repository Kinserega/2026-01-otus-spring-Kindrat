package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с авторами")
@DataJpaTest
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("find author by id")
    void shouldFindAuthorById() {
        Author author = new Author();
        author.setFullName("Test Author");
        Author persistedAuthor = testEntityManager.persistFlushFind(author);
        var actualAuthor = authorRepository.findById(persistedAuthor.getId());
        assertThat(actualAuthor).isPresent().get().usingRecursiveComparison().isEqualTo(persistedAuthor);
    }

    @Test
    @DisplayName("return all authors")
    void shouldReturnAllAuthors() {
        Author firstAuthor = new Author();
        firstAuthor.setFullName("Author A");

        Author secondAuthor = new Author();
        secondAuthor.setFullName("Author B");

        testEntityManager.persist(firstAuthor);
        testEntityManager.persist(secondAuthor);
        testEntityManager.flush();
        testEntityManager.clear();

        List<Author> authors = authorRepository.findAll();

        assertThat(authors).extracting(Author::getFullName).contains("Author A", "Author B");
    }

}
