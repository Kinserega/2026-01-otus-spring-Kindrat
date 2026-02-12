package ru.otus.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizServiceImplTest {

    private QuestionDao questionDao;
    private TestOutputService testOutputService;

    @BeforeEach
    void setUp() {
        questionDao = Mockito.mock(QuestionDao.class);
        testOutputService = new TestOutputServiceImpl(questionDao);
    }
    @Test
    void getQuestionsFromDao() {
        List<Question> expectedQuestions = Arrays.asList(new Question("Test question?", Arrays.asList(new Answer("Option 1"), new Answer("Option 2"))));
        when(questionDao.findAll()).thenReturn(expectedQuestions);
        List<Question> actualQuestions = testOutputService.getQuestions();
        assertEquals(expectedQuestions, actualQuestions);
        verify(questionDao, times(1)).findAll();
    }

    @Test
    void getQuestionsList() {
        List<Question> questions = Arrays.asList(new Question("Q?", Arrays.asList(new Answer("A"))));
        when(questionDao.findAll()).thenReturn(questions);
        List<Question> result = testOutputService.getQuestions();
        assertThrows(UnsupportedOperationException.class, () -> result.add(new Question("New", Arrays.asList(new Answer("X")))));
    }
}