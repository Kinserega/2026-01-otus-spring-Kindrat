package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class CommentController {

    private static final String VIEW_BOOK_COMMENTS_FORM = "comments/book_comments";

    private static final String COMMENTS_ATTRIBUTE = "book";

    private static final String BOOR_ATTRIBUTE = "comments";

    private final CommentService commentService;

    private final BookService bookService;

    @RequestMapping(value = "/book_comments", method = RequestMethod.GET)
    public String getCommentsForBook(@RequestParam("id") long id, Model model) {
        BookDto book = bookService.findById(id);
        List<CommentDto> comments = commentService.findAllByBookId(id);
        model.addAttribute(COMMENTS_ATTRIBUTE, book);
        model.addAttribute(BOOR_ATTRIBUTE, comments);

        return VIEW_BOOK_COMMENTS_FORM;
    }
}
