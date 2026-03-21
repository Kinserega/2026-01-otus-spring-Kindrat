package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestService testService;

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
        when(ioService.readStringWithPromptLocalized("TestService.answer.the.questions")).thenReturn("");
        when(ioService.readIntForRangeWithPromptLocalized(1, 2, "TestService.answer.prompt", "TestService.answer.error")).thenReturn(1);
        when(questionDao.findAll()).thenReturn(List.of(question));

        TestResult result = testService.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getRightAnswersCount()).isEqualTo(1);

        verify(ioService).printLine("");
        verify(ioService).readStringWithPromptLocalized("TestService.answer.the.questions");
        verify(ioService).printFormattedLine("%d. %s", 1, "Sample question?");
        verify(ioService).printFormattedLine("   %d) %s", 1, "Answer A");
        verify(ioService).printFormattedLine("   %d) %s", 2, "Answer B");
        verify(ioService).readIntForRangeWithPromptLocalized(1, 2, "TestService.answer.prompt", "TestService.answer.error");
    }

    @Test
    void shouldHandleEmptyQuestionsList() {
        when(ioService.readStringWithPromptLocalized("TestService.answer.the.questions"))
                .thenReturn("");
        when(questionDao.findAll()).thenReturn(List.of());

        TestResult result = testService.executeTestFor(student);

        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).isEmpty();
        assertThat(result.getRightAnswersCount()).isEqualTo(0);

        verify(ioService).printLine("");
        verify(ioService).readStringWithPromptLocalized("TestService.answer.the.questions");
        verify(ioService, never()).printFormattedLine(eq("%d. %s"), anyInt(), org.mockito.ArgumentMatchers.anyString());
        verify(ioService, never()).printFormattedLine(eq("   %d) %s"), anyInt(), org.mockito.ArgumentMatchers.anyString());
        verify(ioService, never()).readIntForRangeWithPrompt(1, 2, "Your answer: ", "Invalid answer. Try again.");
    }
}