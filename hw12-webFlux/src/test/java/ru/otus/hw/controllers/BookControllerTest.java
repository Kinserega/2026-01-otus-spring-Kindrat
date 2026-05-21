package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Тестирование контроллера книг")
@WebFluxTest(BookController.class)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @Test
    @DisplayName("Должен вернуть список книг")
    void shouldReturnAllBooks() {
        BookDto book = createBookDto();

        given(bookService.findAll()).willReturn(Flux.just(book));

        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(1)
                .contains(book);
    }

    @Test
    @DisplayName("Должен вернуть книгу по id")
    void shouldReturnBookById() {
        BookDto book = createBookDto();

        given(bookService.findById(1L)).willReturn(Mono.just(book));

        webTestClient.get()
                .uri("/api/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Book title")
                .jsonPath("$.author.fullName").isEqualTo("Author name")
                .jsonPath("$.genres[0].name").isEqualTo("Genre name");
    }

    @Test
    @DisplayName("Должен создать книгу")
    void shouldCreateBook() {
        BookCreateDto request = new BookCreateDto("New book", 1L, Set.of(1L, 2L));
        BookDto response = createBookDto();

        given(bookService.insert(request)).willReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Book title");

        then(bookService).should().insert(request);
    }

    @Test
    @DisplayName("Должен обновить книгу")
    void shouldUpdateBook() {
        BookUpdateDto request = new BookUpdateDto("Updated book", 1L, Set.of(1L));
        BookDto response = new BookDto(
                1L,
                "Updated book",
                new AuthorDto(1L, "Author name"),
                List.of(new GenreDto(1L, "Genre name"))
        );

        given(bookService.update(1L, request)).willReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/books/1")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Updated book");

        then(bookService).should().update(1L, request);
    }

    @Test
    @DisplayName("Должен удалить книгу")
    void shouldDeleteBook() {
        given(bookService.deleteById(1L)).willReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/books/1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        then(bookService).should().deleteById(1L);
    }

    private BookDto createBookDto() {
        return new BookDto(
                1L,
                "Book title",
                new AuthorDto(1L, "Author name"),
                List.of(new GenreDto(1L, "Genre name"))
        );
    }
}