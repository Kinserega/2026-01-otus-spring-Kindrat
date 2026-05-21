package ru.otus.hw.models;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
@ToString
@EqualsAndHashCode
public class Comment {

    @Id
    private Long id;

    @NotNull
    private String text;

    @NotNull
    private Long bookId;
}