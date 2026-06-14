package ru.otus.hw.domain;

import ru.otus.hw.enums.PaymentStatus;

import java.math.BigDecimal;

public record Payment(
        Long orderId,
        BigDecimal amount,
        PaymentStatus status
) {
}