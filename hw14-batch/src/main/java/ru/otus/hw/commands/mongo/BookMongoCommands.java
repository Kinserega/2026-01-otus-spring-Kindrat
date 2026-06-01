package ru.otus.hw.commands.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.mongo.BookMongoConverter;
import ru.otus.hw.services.mongo.BookMongoService;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class BookMongoCommands {

    private final BookMongoService bookMongoService;

    private final BookMongoConverter bookMongoConverter;

    @ShellMethod(value = "Find all books from mongo", key = "mab")
    public String findAllBooks() {
        return bookMongoService.findAll().stream()
                .map(bookMongoConverter::bookToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id from mongo", key = "mbbid")
    public String findBookById(String id) {
        return bookMongoService.findById(id)
                .map(bookMongoConverter::bookToString)
                .orElse("Book with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Insert book from mongo", key = "mbins")
    public String insertBook(String title, String authorId, Set<String> genresIds) {
        var savedBook = bookMongoService.insert(title, authorId, genresIds);
        return bookMongoConverter.bookToString(savedBook);
    }

    @ShellMethod(value = "Update book from mongo", key = "mbupd")
    public String updateBook(String id, String title, String authorId, Set<String> genresIds) {
        var savedBook = bookMongoService.update(id, title, authorId, genresIds);
        return bookMongoConverter.bookToString(savedBook);
    }

    @ShellMethod(value = "Delete book by id from mongo", key = "mbdel")
    public void deleteBook(String id) {
        bookMongoService.deleteById(id);
    }
}
