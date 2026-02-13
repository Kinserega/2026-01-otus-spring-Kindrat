package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

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
            ioService.printLine("No questions. Check your CSV file.");
            return;
        }

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            ioService.printFormattedLine("%d. %s", i + 1, question.text());
            getAnswers(question.answers());
            ioService.printLine("");
        }
    }

    private void getAnswers(List<ru.otus.hw.domain.Answer> answers) {
        if (answers == null || answers.isEmpty()) {
            ioService.printLine("No answers available");
            return;
        }
        for (int i = 0; i < answers.size(); i++) {
            char optionLetter = (char) ('a' + i);
            String answerText = answers.get(i).text();
            ioService.printFormattedLine("   %c) %s", optionLetter, answerText);
        }
    }

}
