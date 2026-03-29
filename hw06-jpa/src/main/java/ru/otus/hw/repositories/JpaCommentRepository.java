package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Comment> findById(long id) {
        try {
            Comment comment = entityManager.createQuery("""
                    select c from Comment c
                    join fetch c.book where c.id = :id""", Comment.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(comment);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        return entityManager.createQuery("""
                select c from Comment c
                join fetch c.book where c.book.id = :bookId""", Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null || comment.getId() == 0L) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Comment comment = entityManager.find(Comment.class, id);
        if (comment != null) {
            entityManager.remove(comment);
        }
    }
}
