package ru.otus.spring.service;

import ru.otus.spring.domain.Question;

import java.util.List;

public interface TestOutputService {

    List<Question> getQuestions();

    void printQuestions();

}
