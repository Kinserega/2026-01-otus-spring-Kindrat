package ru.otus.hw.domain;

import ru.otus.hw.enums.DeliveryStatus;

public record Delivery(
        Long orderId,
        String deliveryAddress,
        String trackingNumber,
        DeliveryStatus status
) {
}