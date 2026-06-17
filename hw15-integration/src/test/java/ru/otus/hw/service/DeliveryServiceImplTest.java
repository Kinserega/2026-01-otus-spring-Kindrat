package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.domain.Delivery;
import ru.otus.hw.domain.Payment;
import ru.otus.hw.enums.DeliveryStatus;
import ru.otus.hw.enums.PaymentStatus;
import ru.otus.hw.services.DeliveryService;
import ru.otus.hw.services.impl.DeliveryServiceImpl;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тестирование сервиса доставки")
class DeliveryServiceImplTest {

    private final DeliveryService deliveryService = new DeliveryServiceImpl();

    @Test
    @DisplayName("Должен создать доставку")
    void shouldCreateDelivery() {
        CustomerOrder order = new CustomerOrder(
                1L,
                "Иван",
                "Ноутбук",
                1,
                BigDecimal.valueOf(50_000),
                "Москва"
        );
        Payment payment = new Payment(
                1L,
                BigDecimal.valueOf(50_000),
                PaymentStatus.SUCCESS
        );
        Delivery delivery = deliveryService.createDelivery(payment, order);
        assertThat(delivery.orderId()).isEqualTo(1L);
        assertThat(delivery.status()).isEqualTo(DeliveryStatus.CREATED);
        assertThat(delivery.trackingNumber()).isNotBlank();
    }
}