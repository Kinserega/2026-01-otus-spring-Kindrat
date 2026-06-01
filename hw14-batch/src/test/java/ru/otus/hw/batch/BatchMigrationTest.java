package ru.otus.hw.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.AuthorDocument;
import ru.otus.hw.models.mongo.BookDocument;
import ru.otus.hw.models.mongo.CommentDocument;
import ru.otus.hw.models.mongo.GenreDocument;
import ru.otus.hw.repositories.jpa.AuthorRepository;
import ru.otus.hw.repositories.jpa.BookRepository;
import ru.otus.hw.repositories.jpa.CommentRepository;
import ru.otus.hw.repositories.jpa.GenreRepository;
import ru.otus.hw.repositories.mongo.AuthorMongoRepository;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static ru.otus.hw.batch.config.MigrationJobConfig.MIGRATION_JOB_NAME;

@SpringBootTest(properties = {
        "spring.shell.interactive.enabled=false",
        "spring.shell.command.version.enabled=false",
        "spring.batch.job.enabled=false"
})
@SpringBatchTest
@DisplayName("Тестирование Spring Batch миграции")
class BatchMigrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job migrationJob;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthorMongoRepository authorMongoRepository;

    @Autowired
    private GenreMongoRepository genreMongoRepository;

    @Autowired
    private BookMongoRepository bookMongoRepository;

    @Autowired
    private CommentMongoRepository commentMongoRepository;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @DisplayName("Должен мигрировать авторов, жанры, книги и комментарии")
    void shouldMigrateLibraryData() throws Exception {
        Job job = jobLauncherTestUtils.getJob();

        assertNotNull(job);
        assertEquals(MIGRATION_JOB_NAME, job.getName());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters());

        assertEquals(COMPLETED, jobExecution.getStatus());

        List<Author> dbAuthors = getAuthors();
        List<Genre> dbGenres = getGenres();
        List<Book> dbBooks = getBooks();
        List<Comment> dbComments = getComments();

        List<AuthorDocument> authorDocuments = authorMongoRepository.findAll();
        List<GenreDocument> genreDocuments = genreMongoRepository.findAll();
        List<BookDocument> bookDocuments = bookMongoRepository.findAll();
        List<CommentDocument> commentDocuments = commentMongoRepository.findAll();

        assertAuthorsMigrated(dbAuthors, authorDocuments);
        assertGenresMigrated(dbGenres, genreDocuments);
        assertBooksMigrated(dbBooks, bookDocuments);
        assertCommentsMigrated(dbComments, dbBooks, commentDocuments);
    }

    private List<Author> getAuthors() {
        return authorRepository.findAll().stream()
                .sorted(Comparator.comparing(Author::getId))
                .toList();
    }

    private List<Genre> getGenres() {
        return genreRepository.findAll().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
    }

    private List<Book> getBooks() {
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparing(Book::getId))
                .toList();
    }

    private List<Comment> getComments() {
        return commentRepository.findAll().stream()
                .sorted(Comparator.comparing(Comment::getId))
                .toList();
    }

    private void assertAuthorsMigrated(List<Author> dbAuthors, List<AuthorDocument> authorDocuments) {
        assertThat(authorDocuments).isNotEmpty();
        assertThat(authorDocuments).hasSize(dbAuthors.size());
        assertEquals(
                dbAuthors.stream().map(Author::getFullName).toList(),
                authorDocuments.stream().map(AuthorDocument::getFullName).toList()
        );
    }

    private void assertGenresMigrated(List<Genre> dbGenres, List<GenreDocument> genreDocuments) {
        assertThat(genreDocuments).isNotEmpty();
        assertThat(genreDocuments).hasSize(dbGenres.size());
        assertEquals(
                dbGenres.stream().map(Genre::getName).toList(),
                genreDocuments.stream().map(GenreDocument::getName).toList()
        );
    }

    private void assertBooksMigrated(List<Book> dbBooks, List<BookDocument> bookDocuments) {
        assertThat(bookDocuments).isNotEmpty();
        assertThat(bookDocuments).hasSize(dbBooks.size());
        dbBooks.forEach(book -> {
            BookDocument document = findBookDocumentByTitle(bookDocuments, book.getTitle());
            assertEquals(book.getTitle(), document.getTitle());
            assertNotNull(document.getAuthorDocument());
            assertEquals(book.getAuthor().getFullName(), document.getAuthorDocument().getFullName());
            assertEquals(book.getGenres().stream().map(Genre::getName).toList(), document.getGenreDocuments().stream().map(GenreDocument::getName).toList());
        });
    }

    private void assertCommentsMigrated(List<Comment> dbComments,
                                        List<Book> dbBooks,
                                        List<CommentDocument> commentDocuments) {
        assertThat(commentDocuments).isNotEmpty();
        assertThat(commentDocuments).hasSize(dbComments.size());
        dbComments.forEach(comment -> {
            CommentDocument document = findCommentDocumentByText(commentDocuments, comment.getText());
            assertEquals(comment.getText(), document.getText());
            assertNotNull(document.getBookDocument());
            Book sourceBook = findBookById(dbBooks, comment.getBook().getId());
            assertEquals(sourceBook.getTitle(), document.getBookDocument().getTitle());
        });
    }

    private Book findBookById(List<Book> books, long bookId) {
        return books.stream()
                .filter(book -> book.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Book with id %d not found".formatted(bookId)));
    }

    private BookDocument findBookDocumentByTitle(List<BookDocument> bookDocuments, String title) {
        return bookDocuments.stream()
                .filter(bookDocument -> bookDocument.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new AssertionError("BookDocument with title %s not found".formatted(title)));
    }

    private CommentDocument findCommentDocumentByText(List<CommentDocument> commentDocuments, String text) {
        return commentDocuments.stream()
                .filter(commentDocument -> commentDocument.getText().equals(text))
                .findFirst()
                .orElseThrow(() -> new AssertionError("CommentDocument with text %s not found".formatted(text)));
    }
}