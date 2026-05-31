package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import static org.mockito.BDDMockito.given;

@DisplayName("Тестирование контроллера комментариев")
@WebFluxTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CommentService commentService;

    @Test
    @DisplayName("Должен вернуть комментарии книги")
    void shouldReturnCommentsForBook() {
        CommentDto comment = new CommentDto(1L, "Comment text");

        given(commentService.findAllByBookId(1L)).willReturn(Flux.just(comment));

        webTestClient.get()
                .uri("/api/comments/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CommentDto.class)
                .hasSize(1)
                .contains(comment);
    }
}