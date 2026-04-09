package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBookId(long bookId);

    @Modifying
    @Query("update Comment c set c.text = :text where c.id = :id")
    void update(@Param("id")long id, @Param("text")String text);

}