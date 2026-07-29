package ru.otus.hw.finance_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.finance_service.entity.UserBotState;

import java.util.Optional;

public interface UserBotStateRepository extends JpaRepository<UserBotState, Long> {

    Optional<UserBotState> findByUserId(Long userId);
}