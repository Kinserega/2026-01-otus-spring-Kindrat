package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.exceptions.TestProcessException;

@Component
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final IOService ioService;

    @Override
    public void run() {
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException e) {
            ioService.printLine("Error: failed to load test questions.");
        } catch (TestProcessException e) {
            ioService.printLine("Error: failed execute test.");
        } catch (Exception e) {
            ioService.printLine("Critical application error.");
        }
    }
}