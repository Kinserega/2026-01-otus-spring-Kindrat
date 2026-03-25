package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class CsvQuestionDaoTest {

    @MockitoBean
    private AppProperties properties;

    @Autowired
    private CsvQuestionDao dao;

    @BeforeEach
    void setUp() {
        when(properties.getTestFileName()).thenReturn("questions.csv");
    }

    @Test
    void shouldLoadQuestionsFromCsvFile() {
        List<Question> questions = dao.findAll();
        assertThat(questions).isNotEmpty();
        assertThat(questions).hasSize(7);
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        when(properties.getTestFileName()).thenReturn("non-existent-file.csv");
        assertThatThrownBy(dao::findAll).isInstanceOf(QuestionReadException.class);
    }
}