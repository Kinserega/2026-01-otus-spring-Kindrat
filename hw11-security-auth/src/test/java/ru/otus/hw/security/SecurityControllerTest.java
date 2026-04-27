package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.controllers.*;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тесты контроллеров")
@WebMvcTest({
        BookController.class,
        AuthorController.class,
        GenreController.class,
        CommentController.class
})
@Import(SecurityConfiguration.class)
class SecurityControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен получать список книг")
    void anUnauthenticatedUserShouldNotGetBooksList() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен получать книгу по id")
    void anUnauthenticatedUserShouldNotGetBookDetails() throws Exception {
        mvc.perform(get("/api/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен создавать книгу")
    void anUnauthenticatedUserShouldNotBookCreation() throws Exception {
        mvc.perform(post("/api/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен обновлять книгу")
    void anUnauthenticatedUserShouldNotBookUpdate() throws Exception {
        mvc.perform(put("/api/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен удалять книгу")
    void anUnauthenticatedUserShouldNotBookDeletion() throws Exception {
        mvc.perform(delete("/api/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен получать авторов")
    void anUnauthenticatedUserShouldNotGetAuthors() throws Exception {
        mvc.perform(get("/api/authors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен получать жанры")
    void anUnauthenticatedUserShouldNotGetGenres() throws Exception {
        mvc.perform(get("/api/genres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь не должен получать комментарии книги")
    void anUnauthenticatedUserShouldNotGetBookComments() throws Exception {
        mvc.perform(get("/api/books/1/comments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @Test
    @DisplayName("Аутентифицированный пользователь должен иметь доступ к списку книг")
    void shouldAllowAuthenticatedUserToGetBooks() throws Exception {
        mvc.perform(get("/api/books").with(user("user")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Аутентифицированный пользователь должен иметь доступ к списку авторов")
    void shouldAllowAuthenticatedUserToGetAuthors() throws Exception {
        mvc.perform(get("/api/authors").with(user("user")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Аутентифицированный пользователь должен иметь доступ к списку жанров")
    void shouldAllowAuthenticatedUserToGetGenres() throws Exception {
        mvc.perform(get("/api/genres").with(user("user")))
                .andExpect(status().isOk());
    }
}