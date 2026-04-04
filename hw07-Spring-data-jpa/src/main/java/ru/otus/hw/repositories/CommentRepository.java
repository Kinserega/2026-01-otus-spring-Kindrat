package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findById(long id);

    List<Comment> findAllByBookId(long bookId);

    @Modifying
    @Query("update Comment c set c.text = :text where c.id = :id")
    void update(@Param("id")long id, @Param("text")String text);

    void deleteById(long id);

}