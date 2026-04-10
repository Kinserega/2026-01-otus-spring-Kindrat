package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Override
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toDto);
    }

    @Override
    public List<CommentDto> findAllByBookId(long bookId) {
        return commentMapper.toDtoList(
                commentRepository.findAllByBookId(bookId));
    }

    @Override
    @Transactional
    public CommentDto insert(String text, long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment();
        comment.setText(text);
        comment.setBook(book);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto update(long id, String text) {
        Comment updatedComment = commentRepository.update(id, text);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
