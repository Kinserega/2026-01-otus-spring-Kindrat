package ru.otus.hw.shell;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.service.TestRunnerService;

import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class ShellCommandsTest {

    @MockitoBean
    private TestRunnerService testRunnerService;

    @Autowired
    private TestShellCommands commands;

    @Test
    void shouldRunTest() {
        commands.startTest();
        verify(testRunnerService).run();
    }
}