package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "authors")
public class Author {

    @Id
    private String id;

    private String fullName;
}
