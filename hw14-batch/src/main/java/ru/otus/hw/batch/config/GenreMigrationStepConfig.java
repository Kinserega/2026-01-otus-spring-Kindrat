package ru.otus.hw.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.cache.MigrationDocumentCache;
import ru.otus.hw.batch.model.CachedMigrationItem;
import ru.otus.hw.batch.writer.CachedMongoItemWriter;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.GenreDocument;
import ru.otus.hw.repositories.jpa.GenreRepository;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class GenreMigrationStepConfig {

    private static final int CHUNK_SIZE = 5;

    private static final int PAGE_SIZE = 20;

    private final GenreRepository genreRepository;

    private final MongoTemplate mongoTemplate;

    private final MigrationDocumentCache migrationDocumentCache;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step genreMigrationStep() {
        return new StepBuilder("genreMigrationStep", jobRepository)
                .<Genre, CachedMigrationItem<GenreDocument>>chunk(CHUNK_SIZE, transactionManager)
                .reader(genreReader())
                .processor(genreProcessor())
                .writer(genreWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public RepositoryItemReader<Genre> genreReader() {
        return new RepositoryItemReaderBuilder<Genre>()
                .name("genreReader")
                .repository(genreRepository)
                .methodName("findAll")
                .pageSize(PAGE_SIZE)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Genre, CachedMigrationItem<GenreDocument>> genreProcessor() {
        return genre -> new CachedMigrationItem<>(
                genre.getId(),
                new GenreDocument(null, genre.getName())
        );
    }

    @Bean
    public CachedMongoItemWriter<GenreDocument> genreWriter() {
        return new CachedMongoItemWriter<>(
                mongoTemplate,
                migrationDocumentCache::putGenre
        );
    }
}