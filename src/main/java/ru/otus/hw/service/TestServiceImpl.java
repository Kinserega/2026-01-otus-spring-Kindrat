package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        if (questions.isEmpty()) {
            throw new QuestionReadException("No questions found, check file");
        }
        outputQuestionsWithAnswer(questions);
    }

    private void outputQuestionsWithAnswer(List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String questionToString = convertQuestionToString(i + 1, question.text());
            ioService.printLine(questionToString);
            outputAnswers(question.answers());
            ioService.printLine("");
        }
    }

    private void outputAnswers(List<Answer> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new QuestionReadException("No answer by questions, check file");
        }
        for (int i = 0; i < answers.size(); i++) {
            String answerText = answers.get(i).text();
            String answer = convertAnswerToString(i + 1, answerText);
            ioService.printLine(answer);
        }
    }

    private String convertQuestionToString(int number, String text) {
        return String.format("%d. %s", number, text);
    }

    private String convertAnswerToString(int number, String text) {
        return String.format("   %d) %s", number, text);
    }

}
