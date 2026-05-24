package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.CommentEntity;

public interface CommentRepository extends ReactiveCrudRepository<CommentEntity, Long> {

    Flux<Comment> findAllByBookId(Long bookId);
}