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
            String questionWithAnswerToString = convertQuestionToString(i + 1, question);
            ioService.printLine(questionWithAnswerToString);
        }
    }

    private String convertQuestionToString(int number, Question question) {
        String questionHeader = convertQuestionHeaderToString(number, question.text());
        String answersString = convertAnswersToString(question.answers());
        return questionHeader + System.lineSeparator() + answersString;
    }

    private String convertAnswersToString(List<ru.otus.hw.domain.Answer> answers) {
        StringBuilder answersBuilder = new StringBuilder();
        for (int i = 0; i < answers.size(); i++) {
            appendAnswerWithNewline(answersBuilder, answers, i);
        }
        return answersBuilder.toString();
    }

    private void appendAnswerWithNewline(StringBuilder answersBuilder, List<Answer> answers, int index) {
        String answerText = answers.get(index).text();
        String formattedAnswer = convertAnswerToString(index + 1, answerText);
        answersBuilder.append(formattedAnswer);
        if (index < answers.size() - 1) {
            answersBuilder.append(System.lineSeparator());
        }
    }

    private String convertQuestionHeaderToString(int number, String text) {
        return String.format("%d. %s", number, text);
    }

    private String convertAnswerToString(int number, String text) {
        return String.format("   %d) %s", number, text);
    }

}
