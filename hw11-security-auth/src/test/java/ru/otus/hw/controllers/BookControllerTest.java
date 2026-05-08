package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Тестирование контроллера книг")
@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    private static final long FIRST_BOOK_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Test
    @DisplayName("Должен вернуть все книги")
    void shouldReturnAllBooks() throws Exception {
        List<BookDto> books = List.of(new BookDto(FIRST_BOOK_ID, "Title", null, null));
        given(bookService.findAll()).willReturn(books);
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title"));
    }

    @Test
    @DisplayName("Должен вернуть книгу по id")
    void shouldReturnBookById() throws Exception {
        BookDto book = new BookDto(FIRST_BOOK_ID, "Title", null, null);
        given(bookService.findById(1L)).willReturn(book);
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("Должен создать книгу")
    void shouldCreateBook() throws Exception {
        BookCreateDto requestDto = new BookCreateDto("New Title", 1L, Set.of(1L, 2L));
        BookDto responseDto = new BookDto(1L, "New Title", new AuthorDto(1, "Author 1"), List.of(new GenreDto(1L,"Genre 1")));
        given(bookService.insert(any(BookCreateDto.class))).willReturn(responseDto);
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Должен обновить книгу")
    void shouldUpdateBook() throws Exception {
        BookUpdateDto request = new BookUpdateDto("Updated Title", 2L, Set.of(3L));
        BookDto response = new BookDto(FIRST_BOOK_ID, "Updated Title", null, null);
        given(bookService.update(eq(FIRST_BOOK_ID), any(BookUpdateDto.class))).willReturn(response);
        mockMvc.perform(put("/api/books/{id}", FIRST_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
        then(bookService).should().update(eq(FIRST_BOOK_ID), any(BookUpdateDto.class));
    }

    @Test
    @DisplayName("Должен удалить книгу")
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}