package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import static org.mockito.BDDMockito.given;

@DisplayName("Тестирование контроллера авторов")
@WebFluxTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("Должен вернуть список авторов")
    void shouldReturnAuthors() {
        AuthorDto author = new AuthorDto(1L, "Author name");

        given(authorService.findAll()).willReturn(Flux.just(author));

        webTestClient.get()
                .uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class)
                .hasSize(1)
                .contains(author);
    }
}