package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.DBRef;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "skindrat", runAlways = true)
    public void dropDb(MongoTemplate mongoTemplate) {
        mongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "skindrat")
    public void insertAuthors(MongoTemplate mongoTemplate) {
        mongoTemplate.insert(List.of(
                createAuthorDocument("1", "Author_1"),
                createAuthorDocument("2", "Author_2"),
                createAuthorDocument("3", "Author_3")
        ), "authors");
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "skindrat")
    public void insertGenres(MongoTemplate mongoTemplate) {
        mongoTemplate.insert(List.of(
                createGenreDocument("1", "Genre_1"),
                createGenreDocument("2", "Genre_2"),
                createGenreDocument("3", "Genre_3"),
                createGenreDocument("4", "Genre_4"),
                createGenreDocument("5", "Genre_5"),
                createGenreDocument("6", "Genre_6")
        ), "genres");
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "skindrat")
    public void insertBooks(MongoTemplate mongoTemplate) {
        mongoTemplate.insert(List.of(
                createBookDocument("1", "BookTitle_1", "1", List.of("1", "2")),
                createBookDocument("2", "BookTitle_2", "2", List.of("3", "4")),
                createBookDocument("3", "BookTitle_3", "3", List.of("5", "6"))
        ), "books");
    }

    @ChangeSet(order = "005", id = "insertComments", author = "skindrat")
    public void insertComments(MongoTemplate mongoTemplate) {
        mongoTemplate.insert(List.of(
                createCommentDocument("1", "Comment_1", "1"),
                createCommentDocument("2", "Comment_2", "2"),
                createCommentDocument("3", "Comment_3", "3")
        ), "comments");
    }

    private Document createAuthorDocument(String id, String fullName) {
        return new Document()
                .append("_id", id)
                .append("fullName", fullName);
    }

    private Document createGenreDocument(String id, String name) {
        return new Document()
                .append("_id", id)
                .append("name", name);
    }

    private Document createBookDocument(String id, String title, String authorId, List<String> genreIds) {
        return new Document()
                .append("_id", id)
                .append("title", title)
                .append("author", createDbRef("authors", authorId))
                .append("genres", genreIds.stream()
                        .map(genreId -> createDbRef("genres", genreId))
                        .toList());
    }

    private Document createCommentDocument(String id, String text, String bookId) {
        return new Document()
                .append("_id", id)
                .append("text", text)
                .append("book", createDbRef("books", bookId));
    }

    private DBRef createDbRef(String collectionName, String id) {
        return new DBRef(collectionName, id);
    }
}