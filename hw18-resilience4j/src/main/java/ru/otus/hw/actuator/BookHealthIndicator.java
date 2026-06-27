package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.BookService;

@Component
@RequiredArgsConstructor
public class BookHealthIndicator implements HealthIndicator {

    private final BookService bookService;

    @Override
    public Health health() {
        try {
            long booksCount = bookService.count();
            if (booksCount == 0) {
                return Health.down()
                        .withDetail("reason", "No books found in catalog")
                        .build();
            }
            return Health.up()
                    .withDetail("books", booksCount)
                    .withDetail("status", "Library catalog is available")
                    .build();
        } catch (Exception exception) {
            return Health.down()
                    .withException(exception)
                    .withDetail("status", "Library catalog is unavailable")
                    .build();
        }
    }
}