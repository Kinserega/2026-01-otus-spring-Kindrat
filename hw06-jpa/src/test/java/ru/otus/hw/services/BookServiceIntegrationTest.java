package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookConverter bookConverter;

    @Test
    @DisplayName("Должен возвращать DTO книги с доступными автором и жанрами вне сервиса")
    void shouldProvideAccessToBookRelationsOutsideService() {
        BookDto book = bookService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            assertThat(book.id()).isPositive();
            assertThat(book.title()).isNotBlank();

            assertThat(book.author()).isNotNull();
            assertThat(book.author().id()).isPositive();
            assertThat(book.author().fullName()).isNotBlank();

            assertThat(book.genres()).isNotNull();
            assertThat(book.genres()).isNotEmpty();
            book.genres().forEach(genre -> {
                assertThat(genre).isNotNull();
                assertThat(genre.id()).isPositive();
                assertThat(genre.name()).isNotBlank();
            });
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен возвращать список DTO книг с доступными автором и жанрами вне сервиса")
    void shouldProvideAccessToBooksRelationsOutsideService() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).isNotEmpty();

        assertThatCode(() -> books.forEach(book -> {
            assertThat(book.id()).isPositive();
            assertThat(book.title()).isNotBlank();

            assertThat(book.author()).isNotNull();
            assertThat(book.author().id()).isPositive();
            assertThat(book.author().fullName()).isNotBlank();

            assertThat(book.genres()).isNotNull();
            book.genres().forEach(genre -> {
                assertThat(genre).isNotNull();
                assertThat(genre.id()).isPositive();
                assertThat(genre.name()).isNotBlank();
            });
        })).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен позволять конвертировать DTO книги в строку вне сервиса")
    void shouldConvertBookToStringOutsideService() {
        BookDto book = bookService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            String result = bookConverter.bookToString(book);
            assertThat(result).isNotBlank();
            assertThat(result).contains(book.title());
            assertThat(result).contains(book.author().fullName());
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен позволять конвертировать список DTO книг в строковое представление вне сервиса")
    void shouldConvertBooksToStringOutsideService() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).isNotEmpty();
        assertThatCode(() -> books.forEach(book -> {
            String result = bookConverter.bookToString(book);

            assertThat(result).isNotBlank();
            assertThat(result).contains(book.title());
            assertThat(result).contains(book.author().fullName());
        })).doesNotThrowAnyException();
    }
}
