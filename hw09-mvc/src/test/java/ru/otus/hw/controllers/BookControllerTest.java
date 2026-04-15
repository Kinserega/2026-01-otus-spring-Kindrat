package ru.otus.hw.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Тестирование контроллера книг")
@WebMvcTest(BookController.class)
class BookControllerTest {

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private BookMapper bookMapper;

    @DisplayName("Должен вернуть страницу со списком книг")
    @Test
    void shouldReturnBooksPage() throws Exception {
        given(bookService.findAll()).willReturn(List.of(createBookDto()));
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/all_books"))
                .andExpect(model().attributeExists("books"));
    }

    @DisplayName("Должен добавить новую книгу")
    @Test
    void shouldAddNewBook() throws Exception {
        BookCreateDto bookCreateDto = new BookCreateDto(null, "Book title", 1L, Set.of(1L, 2L));
        mvc.perform(post("/create_book").flashAttr("book", bookCreateDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(bookService).insert(
                bookCreateDto.title(),
                bookCreateDto.authorId(),
                bookCreateDto.genreIds()
        );
    }

    @DisplayName("Должен обновить книгу")
    @Test
    void shouldUpdateBook() throws Exception {
        BookUpdateDto bookUpdateDto = new BookUpdateDto(FIRST_BOOK_ID, "Updated title", 1L, Set.of(1L, 2L));
        mvc.perform(post("/update_book")
                        .param("id", String.valueOf(FIRST_BOOK_ID))
                        .flashAttr("book", bookUpdateDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(bookService).update(FIRST_BOOK_ID, bookUpdateDto.title(), bookUpdateDto.authorId(), bookUpdateDto.genreIds());
    }

    @DisplayName("Должен удалить книгу")
    @Test
    void shouldDeleteBook() throws Exception {
        mvc.perform(post("/delete").param("id", String.valueOf(FIRST_BOOK_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(bookService).deleteById(FIRST_BOOK_ID);
    }

    private BookDto createBookDto() {
        return new BookDto(FIRST_BOOK_ID, "Book title", new AuthorDto(1L, "Author 1"),
                List.of(new GenreDto(1L, "Genre 1"), new GenreDto(2L, "Genre 2")));
    }
}