package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.dto.AuthorDto;
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
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тесты авторизации контроллеров")
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

    @MockitoBean
    private BookMapper bookMapper;

    @DisplayName("Защищённые ресурсы должны перенаправлять неаутентифицированного пользователя на страницу логина")
    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("protectedResources")
    void shouldRedirectAnonymousUserToLogin(HttpMethod method, String url) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @DisplayName("USER должен иметь доступ к страницам чтения")
    @ParameterizedTest(name = "{0} {1} -> {2}")
    @MethodSource("readResources")
    @WithMockUser(username = "user", roles = "USER")
    void shouldAllowUserToReadResources(HttpMethod method,
                                        String url,
                                        int expectedStatus) throws Exception {
        prepareSuccessfulServiceResponses();

        mvc.perform(buildRequest(method, url))
                .andExpect(status().is(expectedStatus));
    }

    @DisplayName("USER не должен иметь доступ к изменению книг")
    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("bookWriteResources")
    @WithMockUser(username = "user", roles = "USER")
    void shouldDenyUserToWriteBooks(HttpMethod method, String url) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().isForbidden());

        verify(bookService, never()).insert(anyString(), anyLong(), anySet());
        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), anySet());
        verify(bookService, never()).deleteById(anyLong());
    }

    @DisplayName("ADMIN должен иметь доступ ко всем защищённым ресурсам")
    @ParameterizedTest(name = "{0} {1} -> {2}")
    @MethodSource("adminResources")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldAllowAdminToAccessProtectedResources(HttpMethod method,
                                                    String url,
                                                    int expectedStatus) throws Exception {
        prepareSuccessfulServiceResponses();

        mvc.perform(buildRequest(method, url))
                .andExpect(status().is(expectedStatus));
    }

    private static Stream<Arguments> protectedResources() {
        return Stream.concat(readResources(), bookWriteResources());
    }

    private static Stream<Arguments> readResources() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, "/", 200),
                Arguments.of(HttpMethod.GET, "/edit_book", 200),
                Arguments.of(HttpMethod.GET, "/edit_book?id=1", 200),
                Arguments.of(HttpMethod.GET, "/authors", 200),
                Arguments.of(HttpMethod.GET, "/genres", 200),
                Arguments.of(HttpMethod.GET, "/book_comments?id=1", 200)
        );
    }

    private static Stream<Arguments> bookWriteResources() {
        return Stream.of(
                Arguments.of(HttpMethod.POST, "/create_book", 200),
                Arguments.of(HttpMethod.POST, "/update_book?id=1", 200),
                Arguments.of(HttpMethod.POST, "/delete?id=1", 302)
        );
    }

    private static Stream<Arguments> adminResources() {
        return Stream.concat(readResources(), bookWriteResources());
    }

    private static MockHttpServletRequestBuilder buildRequest(HttpMethod method, String url) {
        return request(method, url);
    }

    private void prepareSuccessfulServiceResponses() {
        BookDto bookDto = new BookDto(1L, "Book", new AuthorDto(1L, "Author"), List.of(new GenreDto(1L, "Genre")));

        BookUpdateDto bookUpdateDto = new BookUpdateDto(1L, "Book", 1L, Set.of(1L));

        given(bookService.findAll()).willReturn(List.of(bookDto));
        given(bookService.findById(anyLong())).willReturn(bookDto);
        given(bookService.insert(anyString(), anyLong(), anySet())).willReturn(bookDto);
        given(bookService.update(anyLong(), anyString(), anyLong(), anySet())).willReturn(bookDto);

        given(bookMapper.mapToUpdateDto(any(BookDto.class))).willReturn(bookUpdateDto);

        given(authorService.findAll()).willReturn(List.of(new AuthorDto(1L, "Author")));
        given(genreService.findAll()).willReturn(List.of(new GenreDto(1L, "Genre")));
        given(commentService.findAllByBookId(anyLong())).willReturn(List.of());
    }
}