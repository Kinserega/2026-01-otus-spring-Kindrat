package ru.otus.hw.services.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.mongo.CommentDocument;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentMongoServiceImpl implements CommentMongoService {

    private final CommentMongoRepository commentMongoRepository;

    private final BookMongoRepository bookMongoRepository;

    @Override
    public Optional<CommentDocument> findById(String id) {
        return commentMongoRepository.findById(id);
    }

    @Override
    public List<CommentDocument> findAllByBookId(String bookId) {
        return commentMongoRepository.findAllByBookDocumentId(bookId);
    }

    @Override
    @Transactional
    public CommentDocument insert(String text, String bookId) {
        return save(null, text, bookId);
    }

    @Override
    @Transactional
    public CommentDocument update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentMongoRepository.deleteById(id);
    }

    private CommentDocument save(String id, String text, String bookId) {
        var book = bookMongoRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        var comment = new CommentDocument(id, text, book);
        return commentMongoRepository.save(comment);
    }
}
