package ru.otus.hw.services;

import ru.otus.hw.entity.Comment;

import java.util.Optional;

public interface CommentService {

    Optional<Comment> findById(long id);

    Comment insert(String text, long bookId);

    Comment update(long id, String text, long bookId);

    void deleteById(long id);
}
