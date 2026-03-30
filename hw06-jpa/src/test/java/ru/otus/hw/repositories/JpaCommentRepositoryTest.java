package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.entity.Author;
import ru.otus.hw.entity.Book;
import ru.otus.hw.entity.Comment;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Репозиторий для работы с комментариями ")
@DataJpaTest
@Import(JpaCommentRepository.class)
public class JpaCommentRepositoryTest {


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("find comment by id")
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
    @DisplayName("return all comments by book id")
    void shouldReturnAllCommentsByBookId() {
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
}
