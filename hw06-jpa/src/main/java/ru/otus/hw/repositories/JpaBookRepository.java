package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.Book;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(long id) {
        try {
            Book book = entityManager.createQuery("""
                    select distinct b from Book b join fetch b.author
                    left join fetch b.genres where b.id = :id
                    """, Book.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(book);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("""
                select distinct b from Book b
                join fetch b.author left join fetch b.genres""", Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0L) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = entityManager.find(Book.class, id);
        if (book != null) {
            entityManager.remove(book);
        }
    }
}
