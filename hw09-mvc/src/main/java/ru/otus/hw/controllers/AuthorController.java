package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthorController {

    private static final String VIEW_AUTHOR_FORM = "authors/author_list";

    private static final String AUTHORS_ATTRIBUTE = "authors";

    private final AuthorService authorService;

    @RequestMapping(value = "/authors", method = RequestMethod.GET)
    public String getAuthors(Model model) {
        List<AuthorDto> authorDtos = authorService.findAll();
        model.addAttribute(AUTHORS_ATTRIBUTE, authorDtos);
        return VIEW_AUTHOR_FORM;
    }

}
