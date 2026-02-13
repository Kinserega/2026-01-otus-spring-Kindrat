package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    private Question question;

    @BeforeEach
    void setUp() {
        var answers = List.of(new Answer("Answer A", true), new Answer("Answer B", false));
        question = new Question("Sample question?", answers);
    }

    @Test
    void shouldPrintQuestionAndAnswers() {
        when(questionDao.findAll()).thenReturn(List.of(question));
        testService.executeTest();
        verify(ioService).printFormattedLine("%d. %s", 1, "Sample question?");
        verify(ioService).printFormattedLine("   %d) %s", 1, "Answer A");
        verify(ioService).printFormattedLine("   %d) %s", 2, "Answer B");
    }

    @Test
    void shouldHandleEmptyQuestionsList() {
        when(questionDao.findAll()).thenReturn(List.of());
        testService.executeTest();
        verify(ioService).printLine("No questions.");
    }
}