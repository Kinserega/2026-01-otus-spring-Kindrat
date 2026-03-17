package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvQuestionDaoTest {

    private CsvQuestionDao dao;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.setRightAnswersCountToPass(3);
        properties.setLocale("en-US");
        properties.setFileNameByLocaleTag(Map.of(
                "en-US", "questions.csv",
                "ru-RU", "questions_ru.csv"
        ));
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
        AppProperties invalidProperties = new AppProperties();
        invalidProperties.setRightAnswersCountToPass(3);
        invalidProperties.setLocale("en-US");
        invalidProperties.setFileNameByLocaleTag(Map.of("en-US", "non-existent-file.csv"));
        CsvQuestionDao invalidDao = new CsvQuestionDao(invalidProperties);
        assertThatThrownBy(invalidDao::findAll).isInstanceOf(QuestionReadException.class);
    }
}