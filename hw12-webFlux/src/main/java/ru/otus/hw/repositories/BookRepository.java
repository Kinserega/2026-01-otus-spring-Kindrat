package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.otus.hw.models.BookEntity;
import ru.otus.hw.services.BookRepositoryCustom;

public interface BookRepository extends ReactiveCrudRepository<BookEntity, Long>, BookRepositoryCustom {
}
