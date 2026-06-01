package ru.otus.hw.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.batch.core.job.flow.Flow;

@Configuration
@RequiredArgsConstructor
public class MigrationJobConfig {

    public static final String MIGRATION_JOB_NAME = "migrationJob";

    private final JobRepository jobRepository;

    @Bean
    public Job migrationJob(Step cleanUpStep,
                            Flow authorGenreFlow,
                            Step bookMigrationStep,
                            Step commentMigrationStep) {
        return new JobBuilder(MIGRATION_JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(cleanUpFlow(cleanUpStep))
                .next(authorGenreFlow)
                .next(bookMigrationFlow(bookMigrationStep))
                .next(commentMigrationFlow(commentMigrationStep))
                .end()
                .build();
    }

    @Bean
    public Flow cleanUpFlow(Step cleanUpStep) {
        return new FlowBuilder<SimpleFlow>("cleanUpFlow")
                .start(cleanUpStep)
                .build();
    }

    @Bean
    public Flow authorGenreFlow(Flow authorFlow, Flow genreFlow) {
        return new FlowBuilder<SimpleFlow>("authorGenreFlow")
                .split(taskExecutor())
                .add(authorFlow, genreFlow)
                .build();
    }

    @Bean
    public Flow authorFlow(Step authorMigrationStep) {
        return new FlowBuilder<SimpleFlow>("authorFlow")
                .start(authorMigrationStep)
                .build();
    }

    @Bean
    public Flow genreFlow(Step genreMigrationStep) {
        return new FlowBuilder<SimpleFlow>("genreFlow")
                .start(genreMigrationStep)
                .build();
    }

    @Bean
    public Flow bookMigrationFlow(Step bookMigrationStep) {
        return new FlowBuilder<SimpleFlow>("bookMigrationFlow")
                .start(bookMigrationStep)
                .build();
    }

    @Bean
    public Flow commentMigrationFlow(Step commentMigrationStep) {
        return new FlowBuilder<SimpleFlow>("commentMigrationFlow")
                .start(commentMigrationStep)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("migration-");
    }
}
