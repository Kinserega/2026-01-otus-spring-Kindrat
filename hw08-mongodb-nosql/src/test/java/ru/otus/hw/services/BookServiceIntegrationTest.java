package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(BookServiceImpl.class)
class BookServiceIntegrationTest extends AbstractMongoServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("должен находить книгу по id")
    void shouldFindBookById() {
        var actualBook = bookService.findById(firstBook.getId());

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().getTitle()).isEqualTo("Book A");
        assertThat(actualBook.get().getAuthor().getFullName()).isEqualTo("Author A");
        assertThat(actualBook.get().getGenres())
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre A", "Genre B");
    }

    @Test
    @DisplayName("должен возвращать все книги")
    void shouldFindAllBooks() {
        var books = bookService.findAll();

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Book A");
    }

    @Test
    @DisplayName("должен создавать книгу")
    void shouldInsertBook() {
        var book = bookService.insert(
                "New Book",
                secondAuthor.getId(),
                Set.of(firstGenre.getId())
        );
        assertThat(book.getId()).isNotBlank();
        assertThat(book.getTitle()).isEqualTo("New Book");
        assertThat(book.getAuthor().getId()).isEqualTo(secondAuthor.getId());
        assertThat(book.getGenres())
                .extracting(Genre::getId)
                .containsExactly(firstGenre.getId());
    }

    @Test
    @DisplayName("должен обновлять книгу")
    void shouldUpdateBook() {
        var book = bookService.update(
                firstBook.getId(),
                "Updated Book",
                secondAuthor.getId(),
                Set.of(secondGenre.getId())
        );
        assertThat(book.getId()).isEqualTo(firstBook.getId());
        assertThat(book.getTitle()).isEqualTo("Updated Book");
        assertThat(book.getAuthor().getId()).isEqualTo(secondAuthor.getId());
        assertThat(book.getGenres())
                .extracting(Genre::getId)
                .containsExactly(secondGenre.getId());
    }

    @Test
    @DisplayName("должен удалять книгу вместе с комментариями")
    void shouldDeleteBookById() {
        bookService.deleteById(firstBook.getId());
        assertThat(bookRepository.findById(firstBook.getId())).isEmpty();
        assertThat(commentRepository.findAllByBookId(firstBook.getId())).isEmpty();
    }
}