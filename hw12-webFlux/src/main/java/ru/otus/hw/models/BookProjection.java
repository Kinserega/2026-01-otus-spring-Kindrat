package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("books")
@Getter
@AllArgsConstructor
public class BookProjection {

    @Id
    private final Long id;

    @NotNull
    private final String title;

    @NotNull
    @Column("author_id")
    private final Long authorId;
}