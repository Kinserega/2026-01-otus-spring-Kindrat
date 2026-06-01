package ru.otus.hw.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.cache.MigrationDocumentCache;
import ru.otus.hw.models.mongo.AuthorDocument;
import ru.otus.hw.models.mongo.BookDocument;
import ru.otus.hw.models.mongo.CommentDocument;
import ru.otus.hw.models.mongo.GenreDocument;

@Configuration
@RequiredArgsConstructor
public class CleanUpStepConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final MongoTemplate mongoTemplate;

    private final MigrationDocumentCache migrationDocumentCache;

    @Bean
    public Step cleanUpStep() {
        return new StepBuilder("cleanUpStep", jobRepository)
                .tasklet(cleanUpTasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Tasklet cleanUpTasklet() {
        return (contribution, chunkContext) -> {
            mongoTemplate.dropCollection(CommentDocument.class);
            mongoTemplate.dropCollection(BookDocument.class);
            mongoTemplate.dropCollection(GenreDocument.class);
            mongoTemplate.dropCollection(AuthorDocument.class);
            migrationDocumentCache.clear();
            return RepeatStatus.FINISHED;
        };
    }
}