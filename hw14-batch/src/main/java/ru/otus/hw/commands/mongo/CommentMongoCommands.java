package ru.otus.hw.commands.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.mongo.CommentMongoConverter;
import ru.otus.hw.services.mongo.CommentMongoService;

import java.util.stream.Collectors;


@RequiredArgsConstructor
@ShellComponent
public class CommentMongoCommands {

    private final CommentMongoService commentMongoService;

    private final CommentMongoConverter commentMongoConverter;

    @ShellMethod(value = "Find comment  from mongo by id", key = "mcbid")
    public String findCommentById(String id) {
        return commentMongoService.findById(id)
                .map(commentMongoConverter::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find all comments  from mongo by book id", key = "macb")
    public String findAllCommentsByBookId(String bookId) {
        return commentMongoService.findAllByBookId(bookId).stream()
                .map(commentMongoConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
