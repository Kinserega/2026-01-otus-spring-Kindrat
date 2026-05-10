package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

public abstract class AbstractMongoServiceTest {

    @Autowired
    protected MongoOperations mongoOperations;

    @Autowired
    protected AuthorRepository authorRepository;

    @Autowired
    protected GenreRepository genreRepository;

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected CommentRepository commentRepository;

    protected Author firstAuthor;

    protected Author secondAuthor;

    protected Genre firstGenre;

    protected Genre secondGenre;

    protected Book firstBook;

    protected Comment firstComment;

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(Comment.class);
        mongoOperations.dropCollection(Book.class);
        mongoOperations.dropCollection(Genre.class);
        mongoOperations.dropCollection(Author.class);

        firstAuthor = authorRepository.save(new Author("1", "Author A"));
        secondAuthor = authorRepository.save(new Author("2", "Author B"));

        firstGenre = genreRepository.save(new Genre("1", "Genre A"));
        secondGenre = genreRepository.save(new Genre("2", "Genre B"));

        firstBook = bookRepository.save(new Book("1", "Book A", firstAuthor, List.of(firstGenre, secondGenre)));
        firstComment = commentRepository.save(new Comment("1", "Comment A", firstBook));
    }
}