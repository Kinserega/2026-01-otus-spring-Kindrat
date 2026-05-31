package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {

    Flux<CommentDto> findAllByBookId(long bookId);

}
