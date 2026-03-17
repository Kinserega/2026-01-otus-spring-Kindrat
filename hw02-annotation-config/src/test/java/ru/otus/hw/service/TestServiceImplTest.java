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
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    private Student student;

    @BeforeEach
    void setUp() {
        var answers = List.of(new Answer("Answer A", true), new Answer("Answer B", false));
        question = new Question("Sample question?", answers);
        student = new Student("John", "Doe");
    }

    @Test
    void shouldExecuteTestAndReturnResultWithCorrectAnswer() {
        when(ioService.readIntForRangeWithPrompt(1, 2, "Your answer: ", "Invalid answer. Try again.")).thenReturn(1);
        when(questionDao.findAll()).thenReturn(List.of(question));
        TestResult result = testService.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getRightAnswersCount()).isEqualTo(1);

        verify(ioService).printFormattedLine("%d. %s", 1, "Sample question?");
        verify(ioService).printFormattedLine("   %d) %s", 1, "Answer A");
        verify(ioService).printFormattedLine("   %d) %s", 2, "Answer B");
        verify(ioService).readIntForRangeWithPrompt(1, 2, "Your answer: ", "Invalid answer. Try again.");
    }

    @Test
    void shouldHandleEmptyQuestionsList() {
        when(questionDao.findAll()).thenReturn(List.of());
        TestResult result = testService.executeTestFor(student);
        assertThat(result.getAnsweredQuestions()).isEmpty();
        assertThat(result.getRightAnswersCount()).isEqualTo(0);

        verify(ioService, never()).printFormattedLine(eq("%d. %s"), any());
        verify(ioService, never()).printFormattedLine(eq("   %d) %s"), any());
    }
}