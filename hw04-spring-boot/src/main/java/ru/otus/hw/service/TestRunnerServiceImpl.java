package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.TestProcessException;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    @Override
    public void run() {
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException e) {
            ioService.printLineLocalized("Application.error.questions.load");
        } catch (TestProcessException e) {
            ioService.printLineLocalized("Application.error.test.execution");
        } catch (Exception e) {
            ioService.printLineLocalized("Application.error.critical");
        }
    }
}
