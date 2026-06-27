package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Тестирование контроллера жанров")
@WebMvcTest(GenreController.class)
@AutoConfigureMockMvc(addFilters = false)
class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("Должен вернуть список жанров")
    void shouldReturnAllGenres() throws Exception {
        given(genreService.findAll()).willReturn(List.of(
                new GenreDto(1L, "Genre 1")
        ));
        mvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Genre 1"));
    }
}