package ru.otus.hw.commands.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.mongo.GenreMongoConverter;
import ru.otus.hw.services.mongo.GenreMongoService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class GenreMongoCommands {

    private final GenreMongoService genreMongoService;

    private final GenreMongoConverter genreMongoConverter;

    @ShellMethod(value = "Find all genres from mongo", key = "mag")
    public String findAllGenres() {
        return genreMongoService.findAll().stream()
                .map(genreMongoConverter::genreToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
