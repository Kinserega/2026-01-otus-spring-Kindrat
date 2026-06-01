package ru.otus.hw.services.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.AuthorDocument;
import ru.otus.hw.repositories.mongo.AuthorMongoRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorMongoServiceImpl implements AuthorMongoService {

    private final AuthorMongoRepository authorMongoRepository;

    @Override
    public List<AuthorDocument> findAll() {
        return authorMongoRepository.findAll();
    }
}
