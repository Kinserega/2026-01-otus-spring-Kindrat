package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvQuestionDaoTest {

    @Mock
    private AppProperties properties;

    private CsvQuestionDao dao;

    @BeforeEach
    void setUp() {
        when(properties.getTestFileName()).thenReturn("questions.csv");
        dao = new CsvQuestionDao(properties);
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