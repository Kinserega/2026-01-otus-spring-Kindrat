package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.TestProcessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        try {
            ioService.printLine("");
            ioService.readStringWithPromptLocalized("TestService.answer.the.questions");
            var questions = questionDao.findAll();
            var testResult = new TestResult(student);

            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                printQuestionWithNumber(i + 1, question);
                printAnswers(question.answers());

                var userAnswer = getUserAnswer(question.answers().size());
                var isCorrect = checkAnswer(question.answers(), userAnswer);

                testResult.applyAnswer(question, isCorrect);
            }
            return testResult;
        } catch (QuestionReadException e) {
            throw e;
        } catch (Exception e) {
            throw new TestProcessException("Failed test", e);
        }
    }

    private void printQuestionWithNumber(int number, Question question) {
        ioService.printFormattedLine("%d. %s", number, question.text());
    }

    private void printAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("   %d) %s", i + 1, answers.get(i).text());
        }
    }

    private int getUserAnswer(int maxAnswerNumber) {
        return ioService.readIntForRangeWithPromptLocalized(1, maxAnswerNumber,
                "TestService.answer.prompt", "TestService.answer.error");
    }

    private boolean checkAnswer(List<Answer> answers, int answerNumber) {
        var selectedAnswer = answers.get(answerNumber - 1);
        return selectedAnswer.isCorrect();
    }

}
