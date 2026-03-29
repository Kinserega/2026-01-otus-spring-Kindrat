package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами ")
@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("find book by id with author and genres")
    void shouldFindBookById() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Genre firstGenre = new Genre();
        firstGenre.setName("Genre A");
        firstGenre = testEntityManager.persist(firstGenre);

        Genre secondGenre = new Genre();
        secondGenre.setName("Genre B");
        secondGenre = testEntityManager.persist(secondGenre);

        Book book = new Book();
        book.setTitle("Book A");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(List.of(firstGenre, secondGenre)));
        book = testEntityManager.persist(book);

        testEntityManager.flush();
        testEntityManager.clear();

        var actualBook = bookRepository.findById(book.getId());

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().getTitle()).isEqualTo("Book A");
        assertThat(actualBook.get().getAuthor().getFullName()).isEqualTo("Author A");
        assertThat(actualBook.get().getGenres())
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre A", "Genre B");
    }

    @Test
    @DisplayName("return all books with author and genres")
    void shouldReturnAllBooks() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Genre genre = new Genre();
        genre.setName("Genre A");
        genre = testEntityManager.persist(genre);

        Book firstBook = new Book();
        firstBook.setTitle("Book 1");
        firstBook.setAuthor(author);
        firstBook.setGenres(new ArrayList<>(List.of(genre)));
        testEntityManager.persist(firstBook);

        Book secondBook = new Book();
        secondBook.setTitle("Book 2");
        secondBook.setAuthor(author);
        secondBook.setGenres(new ArrayList<>(List.of(genre)));
        testEntityManager.persist(secondBook);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Book> books = bookRepository.findAll();

        assertThat(books)
                .extracting(Book::getTitle)
                .contains("Book 1", "Book 2");
    }
}