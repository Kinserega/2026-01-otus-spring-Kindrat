package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(CommentServiceImpl.class)
class CommentServiceIntegrationTest extends AbstractMongoServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    @DisplayName("должен находить комментарий по id")
    void shouldFindCommentById() {
        var actualComment = commentService.findById(firstComment.getId());

        assertThat(actualComment).isPresent();
        assertThat(actualComment.get().getText()).isEqualTo("Comment A");
        assertThat(actualComment.get().getBook().getId()).isEqualTo(firstBook.getId());
    }

    @Test
    @DisplayName("должен находить все комментарии книги")
    void shouldFindAllCommentsByBookId() {
        var comments = commentService.findAllByBookId(firstBook.getId());

        assertThat(comments)
                .extracting(Comment::getText)
                .containsExactly("Comment A");
    }

    @Test
    @DisplayName("должен создавать комментарий")
    void shouldInsertComment() {
        var comment = commentService.insert("New Comment", firstBook.getId());

        assertThat(comment.getId()).isNotBlank();
        assertThat(comment.getText()).isEqualTo("New Comment");
        assertThat(comment.getBook().getId()).isEqualTo(firstBook.getId());
    }

    @Test
    @DisplayName("должен обновлять комментарий")
    void shouldUpdateComment() {
        var comment = commentService.update(
                firstComment.getId(),
                "Updated Comment",
                firstBook.getId()
        );

        assertThat(comment.getId()).isEqualTo(firstComment.getId());
        assertThat(comment.getText()).isEqualTo("Updated Comment");
        assertThat(comment.getBook().getId()).isEqualTo(firstBook.getId());
    }

    @Test
    @DisplayName("должен удалять комментарий")
    void shouldDeleteCommentById() {
        commentService.deleteById(firstComment.getId());

        assertThat(commentService.findById(firstComment.getId())).isEmpty();
    }
}