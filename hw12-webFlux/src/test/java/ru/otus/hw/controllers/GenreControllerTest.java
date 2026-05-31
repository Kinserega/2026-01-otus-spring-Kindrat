package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import static org.mockito.BDDMockito.given;

@DisplayName("Тестирование контроллера жанров")
@WebFluxTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("Должен вернуть список жанров")
    void shouldReturnGenres() {
        GenreDto genre = new GenreDto(1L, "Genre name");

        given(genreService.findAll()).willReturn(Flux.just(genre));

        webTestClient.get()
                .uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreDto.class)
                .hasSize(1)
                .contains(genre);
    }
}