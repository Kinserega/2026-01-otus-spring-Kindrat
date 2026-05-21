package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Override
    public Mono<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toDto)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Comment with id %d not found".formatted(id)
                )));
    }

    @Override
    public Flux<CommentDto> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId)
                .map(commentMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<CommentDto> insert(String text, long bookId) {
        return validateBookExists(bookId)
                .then(commentRepository.save(new Comment(null, text, bookId)))
                .map(commentMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<CommentDto> update(long id, String text) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Comment with id %d not found".formatted(id)
                )))
                .flatMap(comment -> commentRepository.save(new Comment(comment.getId(), text, comment.getBookId())))
                .map(commentMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return validateCommentExists(id)
                .then(commentRepository.deleteById(id));
    }

    private Mono<Void> validateBookExists(long bookId) {
        return bookRepository.existsById(bookId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id %d not found".formatted(bookId)
                )))
                .then();
    }

    private Mono<Void> validateCommentExists(long id) {
        return commentRepository.existsById(id)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Comment with id %d not found".formatted(id)
                )))
                .then();
    }

}
