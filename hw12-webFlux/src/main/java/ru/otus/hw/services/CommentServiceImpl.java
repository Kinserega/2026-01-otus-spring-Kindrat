package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.repositories.CommentRepository;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public Flux<CommentDto> findAllByBookId(long bookId) {
        return commentRepository.findAllByBookId(bookId)
                .map(commentMapper::toDto);
    }
}
