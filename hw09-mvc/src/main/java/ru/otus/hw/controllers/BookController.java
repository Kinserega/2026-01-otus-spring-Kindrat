package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {
    private static final String BOOKS_VIEW = "books/all_books";

    private static final String EDIT_BOOK_VIEW = "books/edit_book";

    private static final String REDIRECT_TO_ROOT = "redirect:/";

    private static final String BOOKS_ATTRIBUTE = "books";

    private static final String BOOK_ATTRIBUTE = "book";

    private static final String AUTHORS_ATTRIBUTE = "authors";

    private static final String GENRES_ATTRIBUTE = "genres";

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final BookMapper bookMapper;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAllBooks(Model model) {
        model.addAttribute(BOOKS_ATTRIBUTE, bookService.findAll());
        return BOOKS_VIEW;
    }

    @RequestMapping(value = "/edit_book", method = RequestMethod.GET)
    public String showEditBookPage(@RequestParam(value = "id", required = false) Long id, Model model) {
        model.addAttribute(AUTHORS_ATTRIBUTE, authorService.findAll());
        model.addAttribute(GENRES_ATTRIBUTE, genreService.findAll());
        if (id == null) {
            model.addAttribute(BOOK_ATTRIBUTE, new BookCreateDto(null, null, null, null));
        } else {
            BookDto bookDto = bookService.findById(id);
            model.addAttribute(BOOK_ATTRIBUTE, bookMapper.mapToUpdateDto(bookDto));
        }
        return EDIT_BOOK_VIEW;
    }

    @RequestMapping(value = "/create_book", method = RequestMethod.POST)
    public String createBook(@Valid @ModelAttribute("book") BookCreateDto bookCreateDto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(AUTHORS_ATTRIBUTE, authorService.findAll());
            model.addAttribute(GENRES_ATTRIBUTE, genreService.findAll());
            return EDIT_BOOK_VIEW;
        }
        bookService.insert(bookCreateDto.title(), bookCreateDto.authorId(), bookCreateDto.genreIds());
        return REDIRECT_TO_ROOT;
    }

    @RequestMapping(value = "/update_book", method = RequestMethod.POST)
    public String updateBook(@RequestParam("id") long id, @Valid @ModelAttribute("book") BookUpdateDto bookUpdateDto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(AUTHORS_ATTRIBUTE, authorService.findAll());
            model.addAttribute(GENRES_ATTRIBUTE, genreService.findAll());
            return EDIT_BOOK_VIEW;
        }
        bookService.update(id, bookUpdateDto.title(), bookUpdateDto.authorId(), bookUpdateDto.genreIds());
        return REDIRECT_TO_ROOT;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteBook(@RequestParam("id") long id) {
        bookService.deleteById(id);
        return REDIRECT_TO_ROOT;
    }
}
