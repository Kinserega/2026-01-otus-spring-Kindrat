package ru.otus.hw.repositories;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("Репозиторий для работы с комментариями ")
@DataJpaTest
@Import(JpaCommentRepository.class)
public class JpaCommentRepositoryTest {


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("должен находить комментарий по id")
    void shouldFindCommentById() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Book book = new Book();
        book.setTitle("Book A");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>());
        book = testEntityManager.persist(book);

        Comment comment = new Comment();
        comment.setText("Comment A");
        comment.setBook(book);
        comment = testEntityManager.persist(comment);

        testEntityManager.flush();
        testEntityManager.clear();

        var actualComment = commentRepository.findById(comment.getId());

        assertThat(actualComment).isPresent();
        assertThat(actualComment.get().getText()).isEqualTo("Comment A");
        assertThat(actualComment.get().getBook().getTitle()).isEqualTo("Book A");
    }

    @Test
    @DisplayName("должен возвращать пустой Optional, если комментарий не найден")
    void shouldReturnEmptyOptionalWhenCommentNotFound() {
        var actualComment = commentRepository.findById(999L);

        assertThat(actualComment).isEmpty();
    }

    @Test
    @DisplayName("должен находить все комментарии книги по id книги")
    void shouldFindAllCommentsByBookId() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Book book = new Book();
        book.setTitle("Book A");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>());
        book = testEntityManager.persist(book);

        Comment firstComment = new Comment();
        firstComment.setText("Comment 1");
        firstComment.setBook(book);
        testEntityManager.persist(firstComment);

        Comment secondComment = new Comment();
        secondComment.setText("Comment 2");
        secondComment.setBook(book);
        testEntityManager.persist(secondComment);

        testEntityManager.flush();
        testEntityManager.clear();

        List<Comment> comments = commentRepository.findAllByBookId(book.getId());

        assertThat(comments)
                .extracting(Comment::getText)
                .containsExactly("Comment 1", "Comment 2");
    }

    @Test
    @DisplayName("должен сохранять новый комментарий")
    void shouldSaveNewComment() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Book book = new Book();
        book.setTitle("Book A");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>());
        book = testEntityManager.persist(book);

        Comment comment = new Comment();
        comment.setText("New Comment");
        comment.setBook(book);

        Comment savedComment = commentRepository.save(comment);

        testEntityManager.flush();
        testEntityManager.clear();

        Comment actualComment = testEntityManager.find(Comment.class, savedComment.getId());

        assertThat(savedComment.getId()).isPositive();
        assertThat(actualComment).isNotNull();
        assertThat(actualComment.getText()).isEqualTo("New Comment");
    }

    @Test
    @DisplayName("должен обновлять текст комментария")
    void shouldUpdateCommentText() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Book book = new Book();
        book.setTitle("Book A");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>());
        book = testEntityManager.persist(book);

        Comment comment = new Comment();
        comment.setText("Old Text");
        comment.setBook(book);
        comment = testEntityManager.persist(comment);

        testEntityManager.flush();
        testEntityManager.clear();

        Comment updatedComment = commentRepository.update(comment.getId(), "Updated Text");

        testEntityManager.flush();
        testEntityManager.clear();

        Comment actualComment = testEntityManager.find(Comment.class, comment.getId());

        assertThat(updatedComment.getText()).isEqualTo("Updated Text");
        assertThat(actualComment.getText()).isEqualTo("Updated Text");
    }

    @Test
    @DisplayName("должен выбрасывать исключение при обновлении несуществующего комментария")
    void shouldThrowExceptionWhenUpdatingMissingComment() {
        assertThatThrownBy(() -> commentRepository.update(999L, "Updated Text"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Comment with id 999 not found");
    }

    @Test
    @DisplayName("должен удалять комментарий по id")
    void shouldDeleteCommentById() {
        Author author = new Author();
        author.setFullName("Author A");
        author = testEntityManager.persist(author);

        Book book = new Book();
        book.setTitle("Book A");
        book.setAuthor(author);
        book.setGenres(new ArrayList<>());
        book = testEntityManager.persist(book);

        Comment comment = new Comment();
        comment.setText("Comment To Delete");
        comment.setBook(book);
        comment = testEntityManager.persist(comment);

        testEntityManager.flush();
        testEntityManager.clear();

        commentRepository.deleteById(comment.getId());

        testEntityManager.flush();
        testEntityManager.clear();

        Comment actualComment = testEntityManager.find(Comment.class, comment.getId());

        assertThat(actualComment).isNull();
    }
}
