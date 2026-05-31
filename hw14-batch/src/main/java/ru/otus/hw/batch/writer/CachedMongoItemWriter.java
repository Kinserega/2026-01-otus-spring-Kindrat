package ru.otus.hw.batch.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.batch.model.CachedMigrationItem;

import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class CachedMongoItemWriter<T> implements ItemWriter<CachedMigrationItem<T>> {

    private final MongoTemplate mongoTemplate;

    private final BiConsumer<Long, T> cacheWriter;

    private final String collectionName;

    @Override
    public void write(Chunk<? extends CachedMigrationItem<T>> chunk) {
        List<T> documents = chunk.getItems().stream()
                .map(CachedMigrationItem::document)
                .toList();
        mongoTemplate.insert(documents, collectionName);
        chunk.getItems().forEach(item -> {
            cacheWriter.accept(item.sourceId(), item.document());
        });
    }
}