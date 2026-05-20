package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GenreController {

    private static final String VIEW_GENRE_FORM = "genres/genre_list";

    private static final String GENRES_ATTRIBUTE = "genres";

    private final GenreService genreService;

    @RequestMapping(value = "/genres", method = RequestMethod.GET)
    public String getAuthors(Model model) {
        List<GenreDto> genreDtos = genreService.findAll();
        model.addAttribute(GENRES_ATTRIBUTE, genreDtos);
        return VIEW_GENRE_FORM;
    }
}
