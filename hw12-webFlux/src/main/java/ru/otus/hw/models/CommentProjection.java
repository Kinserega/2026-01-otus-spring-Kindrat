package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("comments")
@Getter
@AllArgsConstructor
public class CommentProjection {

    @Id
    private final Long id;

    @NotNull
    private final String text;

    @NotNull
    @Column("book_id")
    private final Long bookId;
}