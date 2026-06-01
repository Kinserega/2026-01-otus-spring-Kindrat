package ru.otus.hw.services.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.GenreDocument;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreMongoServiceImpl implements GenreMongoService {

    private final GenreMongoRepository genreMongoRepository;

    @Override
    public List<GenreDocument> findAll() {
        return genreMongoRepository.findAll();
    }
}
