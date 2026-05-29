package ru.otus.hw.batch.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.batch.model.CachedMigrationItem;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class CachedMongoItemWriter<T> implements ItemWriter<CachedMigrationItem<T>> {

    private final MongoTemplate mongoTemplate;

    private final BiConsumer<Long, T> cacheWriter;

    @Override
    public void write(Chunk<? extends CachedMigrationItem<T>> chunk) {
        chunk.getItems().forEach(item -> {
            T savedDocument = mongoTemplate.insert(item.document());
            cacheWriter.accept(item.sourceId(), savedDocument);
        });
    }
}