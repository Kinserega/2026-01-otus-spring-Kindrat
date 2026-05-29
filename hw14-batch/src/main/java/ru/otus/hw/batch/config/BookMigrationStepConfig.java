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
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.AuthorDocument;
import ru.otus.hw.models.mongo.BookDocument;
import ru.otus.hw.models.mongo.GenreDocument;
import ru.otus.hw.repositories.jpa.BookRepository;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BookMigrationStepConfig {

    private static final int CHUNK_SIZE = 5;

    private static final int PAGE_SIZE = 20;

    private final BookRepository bookRepository;

    private final MongoTemplate mongoTemplate;

    private final MigrationDocumentCache migrationDocumentCache;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step bookMigrationStep() {
        return new StepBuilder("bookMigrationStep", jobRepository)
                .<Book, CachedMigrationItem<BookDocument>>chunk(CHUNK_SIZE, transactionManager)
                .reader(bookReader())
                .processor(bookProcessor())
                .writer(bookWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public RepositoryItemReader<Book> bookReader() {
        return new RepositoryItemReaderBuilder<Book>()
                .name("bookReader")
                .repository(bookRepository)
                .methodName("findAll")
                .pageSize(PAGE_SIZE)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Book, CachedMigrationItem<BookDocument>> bookProcessor() {
        return book -> {
            AuthorDocument authorDocument = migrationDocumentCache.getAuthor(book.getAuthor().getId());

            List<Long> genreIds = book.getGenres() == null
                    ? List.of()
                    : book.getGenres().stream()
                    .map(Genre::getId)
                    .toList();

            List<GenreDocument> genreDocuments = migrationDocumentCache.getGenres(genreIds);

            BookDocument bookDocument = new BookDocument(
                    null,
                    book.getTitle(),
                    authorDocument,
                    genreDocuments
            );

            return new CachedMigrationItem<>(book.getId(), bookDocument);
        };
    }

    @Bean
    public CachedMongoItemWriter<BookDocument> bookWriter() {
        return new CachedMongoItemWriter<>(
                mongoTemplate,
                migrationDocumentCache::putBook
        );
    }
}