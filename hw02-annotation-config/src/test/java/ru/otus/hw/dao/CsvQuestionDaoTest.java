package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvQuestionDaoTest {

    private CsvQuestionDao dao;

    @BeforeEach
    void setUp() {
        var properties = new AppProperties(3, "questions.csv");
        dao = new CsvQuestionDao(properties);
    }

    @Test
    void shouldLoadQuestionsFromCsvFile() {
        List<Question> questions = dao.findAll();
        assertThat(questions).isNotEmpty();
        assertThat(questions).hasSize(7);
        Question firstQuestion = questions.get(0);
        assertThat(firstQuestion.text()).isEqualTo("Is there life on Mars?");
        assertThat(firstQuestion.answers()).hasSize(3);
        assertThat(firstQuestion.answers().get(0).isCorrect()).isTrue();
        assertThat(firstQuestion.answers().get(1).isCorrect()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        var invalidProperties = new AppProperties(3, "non-existent-file.csv");
        var invalidDao = new CsvQuestionDao(invalidProperties);

        assertThatThrownBy(() -> invalidDao.findAll())
                .isInstanceOf(ru.otus.hw.exceptions.QuestionReadException.class)
                .hasMessageContaining("Failed to load questions due to unexpected error");
    }
}