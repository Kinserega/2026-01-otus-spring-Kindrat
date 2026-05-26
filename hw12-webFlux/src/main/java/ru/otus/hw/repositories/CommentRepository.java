package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.CommentProjection;

public interface CommentRepository extends ReactiveCrudRepository<CommentProjection, Long> {

    Flux<Comment> findAllByBookId(Long bookId);
}