package ru.otus.spring.service;

import ru.otus.spring.dao.QuestionDao;
import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import java.util.List;

/**
 * Default implementation of QuizService.
 */
public class TestOutputServiceImpl implements TestOutputService {
    private final QuestionDao questionDao;

    public TestOutputServiceImpl(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public List<Question> getQuestions() {
        return questionDao.findAll();
    }

    @Override
    public void printQuestions() {
        List<Question> questions = getQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions available.");
            return;
        }
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.println("Question " + (i + 1) + ": " + question.getText());
            List<Answer> answers = question.getAnswers();
            for (int j = 0; j < answers.size(); j++) {
                System.out.println("  " + (j + 1) + ". " + answers.get(j).getText());
            }
            System.out.println();
        }
    }
}