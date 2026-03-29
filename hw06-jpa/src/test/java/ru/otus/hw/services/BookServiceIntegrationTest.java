package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("should provide access to book author and genres outside service")
    void shouldNotThrowLazyInitializationExceptionForBookRelations() {
        var book = bookService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            book.getAuthor().getFullName();
            book.getGenres().size();
            book.getGenres().forEach(genre -> genre.getName());
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should provide access to relations for all books outside service")
    void shouldNotThrowLazyInitializationExceptionForBooksListRelations() {
        var books = bookService.findAll();

        assertThatCode(() -> books.forEach(book -> {
            book.getAuthor().getFullName();
            book.getGenres().size();
        })).doesNotThrowAnyException();
    }

}
