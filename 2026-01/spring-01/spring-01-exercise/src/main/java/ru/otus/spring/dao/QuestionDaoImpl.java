package ru.otus.spring.dao;

import ru.otus.spring.domain.Answer;
import ru.otus.spring.domain.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionDaoImpl implements QuestionDao {
    private final String resourceNameFile;

    public QuestionDaoImpl(String resourceNameFile) {
        this.resourceNameFile = resourceNameFile;
    }

    @Override
    public List<Question> findAll() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceNameFile)) {
            return parseCsv(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourceNameFile, e);
        }
    }

    private List<Question> parseCsv(InputStream inputStream) throws IOException {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                questions.add(parseQuestionLine(line));
            }
        }
        return Collections.unmodifiableList(questions);
    }

    private Question parseQuestionLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid line format: " + line);
        }
        String questionText = parts[0].trim();
        List<Answer> answers = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            String answerText = parts[i].trim();
            if (!answerText.isEmpty()) {
                answers.add(new Answer(answerText));
            }
        }
        return new Question(questionText, answers);
    }
}