package ru.otus.hw.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.auth_service.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTelegramId(Long telegramId);

}