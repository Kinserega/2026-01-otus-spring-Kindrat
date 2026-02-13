package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.exceptions.QuestionReadException;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    @Override
    public void run() {
        try {
            testService.executeTest();
        } catch (QuestionReadException questionReadException) {
            System.err.println(questionReadException.getMessage());
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }
}
