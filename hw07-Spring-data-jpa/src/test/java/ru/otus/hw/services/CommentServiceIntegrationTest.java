package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentConverter commentConverter;

    @Test
    @DisplayName("Должен возвращать DTO комментария с доступными данными вне сервиса")
    void shouldProvideAccessToCommentDataOutsideService() {
        CommentDto comment = commentService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            assertThat(comment.id()).isPositive();
            assertThat(comment.text()).isNotBlank();
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен возвращать список DTO комментариев с доступными данными вне сервиса")
    void shouldProvideAccessToCommentsDataOutsideService() {
        List<CommentDto> comments = commentService.findAllByBookId(1L);

        assertThat(comments).isNotEmpty();

        assertThatCode(() -> comments.forEach(comment -> {
            assertThat(comment.id()).isPositive();
            assertThat(comment.text()).isNotBlank();
        })).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен позволять конвертировать DTO комментария в строку вне сервиса")
    void shouldConvertCommentToStringOutsideService() {
        CommentDto comment = commentService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            String result = commentConverter.commentToString(comment);

            assertThat(result).isNotBlank();
            assertThat(result).contains(comment.text());
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Должен позволять конвертировать список DTO комментариев в строку вне сервиса")
    void shouldConvertCommentsToStringOutsideService() {
        List<CommentDto> comments = commentService.findAllByBookId(1L);

        assertThat(comments).isNotEmpty();

        assertThatCode(() -> comments.forEach(comment -> {
            String result = commentConverter.commentToString(comment);

            assertThat(result).isNotBlank();
            assertThat(result).contains(comment.text());
        })).doesNotThrowAnyException();
    }
}