package ru.otus.hw.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.cache.MigrationDocumentCache;

import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.mongo.BookDocument;
import ru.otus.hw.models.mongo.CommentDocument;
import ru.otus.hw.repositories.jpa.CommentRepository;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CommentMigrationStepConfig {

    private static final int CHUNK_SIZE = 5;

    private static final int PAGE_SIZE = 20;

    private final CommentRepository commentRepository;

    private final MongoTemplate mongoTemplate;

    private final MigrationDocumentCache migrationDocumentCache;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step commentMigrationStep() {
        return new StepBuilder("commentMigrationStep", jobRepository)
                .<Comment, CommentDocument>chunk(CHUNK_SIZE, transactionManager)
                .reader(commentReader())
                .processor(commentProcessor())
                .writer(commentWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public RepositoryItemReader<Comment> commentReader() {
        return new RepositoryItemReaderBuilder<Comment>()
                .name("commentReader")
                .repository(commentRepository)
                .methodName("findAll")
                .pageSize(PAGE_SIZE)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Comment, CommentDocument> commentProcessor() {
        return comment -> {
            BookDocument bookDocument = migrationDocumentCache.getBook(comment.getBook().getId());

            return new CommentDocument(
                    null,
                    comment.getText(),
                    bookDocument
            );
        };
    }

    @Bean
    public MongoItemWriter<CommentDocument> commentWriter() {
        return new MongoItemWriterBuilder<CommentDocument>()
                .template(mongoTemplate)
                .collection("comments")
                .build();
    }
}