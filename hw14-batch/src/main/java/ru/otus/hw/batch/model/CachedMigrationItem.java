package ru.otus.hw.batch.model;

public record CachedMigrationItem<T>(
        Long sourceId,
        T document
) {
}