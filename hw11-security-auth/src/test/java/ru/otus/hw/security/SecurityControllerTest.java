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
import ru.otus.hw.controllers.*;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @DisplayName("Публичные ресурсы должны быть доступны без аутентификации")
    @ParameterizedTest(name = "{0} {1} -> {2}")
    @MethodSource("publicResources")
    void shouldAllowAnonymousAccessToPublicResources(HttpMethod method,
                                                     String url,
                                                     int status) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().is(status));
    }

    @DisplayName("Защищённые ресурсы должны перенаправлять неаутентифицированного пользователя на страницу логина")
    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("protectedResources")
    void shouldRedirectAnonymousUserToLogin(HttpMethod method, String url) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login.html"));
    }

    @DisplayName("Защищённые ресурсы должны быть доступны аутентифицированному пользователю")
    @ParameterizedTest(name = "{0} {1} -> {2}")
    @MethodSource("protectedResources")
    @WithMockUser(username = "user")
    void shouldAllowAuthenticatedUserToAccessProtectedResources(HttpMethod method,
                                                                String url,
                                                                int expectedStatus) throws Exception {
        mvc.perform(buildRequest(method, url))
                .andExpect(status().is(expectedStatus));
    }

    private static Stream<Arguments> publicResources() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, "/login.html", 200)
        );
    }

    private static Stream<Arguments> protectedResources() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, "/api/books", 200),
                Arguments.of(HttpMethod.GET, "/api/books/1", 200),
                Arguments.of(HttpMethod.POST, "/api/books", 400),
                Arguments.of(HttpMethod.PUT, "/api/books/1", 400),
                Arguments.of(HttpMethod.DELETE, "/api/books/1", 204),
                Arguments.of(HttpMethod.GET, "/api/authors", 200),
                Arguments.of(HttpMethod.GET, "/api/genres", 200),
                Arguments.of(HttpMethod.GET, "/api/comments/books/1", 200)
        );
    }


    private static MockHttpServletRequestBuilder buildRequest(HttpMethod method, String url) {
        MockHttpServletRequestBuilder requestBuilder = request(method, url);
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE) {
            requestBuilder.with(csrf());
        }
        return requestBuilder;
    }
}