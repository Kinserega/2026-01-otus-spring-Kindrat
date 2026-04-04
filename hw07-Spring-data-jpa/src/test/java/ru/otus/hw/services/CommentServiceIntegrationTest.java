package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThatCode;

@DataJpaTest
@Import(CommentServiceImpl.class)
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("should provide access to comment book outside service")
    void shouldNotThrowLazyInitializationExceptionForCommentBook() {
        var comment = commentService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            comment.getBook().getTitle();
            comment.getBook().getId();
        }).doesNotThrowAnyException();
    }
}