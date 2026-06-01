package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.jpa.BookRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами ")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен возвращать все книги вместе с автором и жанрами")
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
    @DisplayName("должен возвращать все книги вместе с автором и жанрами")
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

    @Test
    @DisplayName("должен возвращать пустой Optional, если книга не найдена")
    void shouldReturnEmptyOptionalWhenBookNotFound() {
        var actualBook = bookRepository.findById(999L);

        assertThat(actualBook).isEmpty();
    }

    @Test
    @DisplayName("должен сохранять новую книгу")
    void shouldSaveNewBook() {
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
        book.setTitle("New Book");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>(List.of(firstGenre, secondGenre)));

        Book savedBook = bookRepository.save(book);

        testEntityManager.flush();
        testEntityManager.clear();

        Book actualBook = testEntityManager.find(Book.class, savedBook.getId());

        assertThat(savedBook.getId()).isPositive();
        assertThat(actualBook).isNotNull();
        assertThat(actualBook.getTitle()).isEqualTo("New Book");
    }

    @Test
    @DisplayName("должен обновлять существующую книгу")
    void shouldUpdateBook() {
        Author firstAuthor = new Author();
        firstAuthor.setFullName("Author A");
        firstAuthor = testEntityManager.persist(firstAuthor);

        Author secondAuthor = new Author();
        secondAuthor.setFullName("Author B");
        secondAuthor = testEntityManager.persist(secondAuthor);

        Genre firstGenre = new Genre();
        firstGenre.setName("Genre A");
        firstGenre = testEntityManager.persist(firstGenre);

        Genre secondGenre = new Genre();
        secondGenre.setName("Genre B");
        secondGenre = testEntityManager.persist(secondGenre);

        Book book = new Book();
        book.setTitle("Old Title");
        book.setAuthor(firstAuthor);
        book.setGenres(new ArrayList<>(List.of(firstGenre)));
        book = testEntityManager.persist(book);

        testEntityManager.flush();
        testEntityManager.clear();

        Book detachedBook = testEntityManager.find(Book.class, book.getId());
        detachedBook.setTitle("Updated Title");
        detachedBook.setAuthor(secondAuthor);
        detachedBook.setGenres(new ArrayList<>(List.of(secondGenre)));

        bookRepository.save(detachedBook);

        testEntityManager.flush();
        testEntityManager.clear();

        Book actualBook = testEntityManager.find(Book.class, book.getId());

        assertThat(actualBook.getTitle()).isEqualTo("Updated Title");
        assertThat(actualBook.getAuthor().getFullName()).isEqualTo("Author B");
    }

    @Test
    @DisplayName("должен удалять книгу по id")
    void shouldDeleteBookById() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Book book = new Book();
        book.setTitle("Book To Delete");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>());
        book = testEntityManager.persist(book);

        testEntityManager.flush();
        testEntityManager.clear();

        bookRepository.deleteById(book.getId());

        testEntityManager.flush();
        testEntityManager.clear();

        Book actualBook = testEntityManager.find(Book.class, book.getId());

        assertThat(actualBook).isNull();
    }
}