package ru.otus.hw.commands.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommmands {

    private final JobLauncher jobLauncher;

    private final Job migrateBooksJob;

    @ShellMethod(value = "Run migration from SQL to MongoDB", key = "start")
    public void startMigration() throws Exception {
        jobLauncher.run(migrateBooksJob, new JobParameters());
    }

}
