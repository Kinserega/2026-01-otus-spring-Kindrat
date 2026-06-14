package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.domain.Payment;
import ru.otus.hw.enums.PaymentStatus;
import ru.otus.hw.services.PaymentService;
import ru.otus.hw.services.impl.PaymentServiceImpl;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тестирование сервиса оплаты")
class PaymentServiceImplTest {

    private final PaymentService paymentService = new PaymentServiceImpl();

    @Test
    @DisplayName("Должен создать успешную оплату")
    void shouldCreateSuccessfulPayment() {
        CustomerOrder order = new CustomerOrder(
                1L,
                "Иван",
                "Ноутбук",
                1,
                BigDecimal.valueOf(50_000),
                "Москва"
        );
        Payment payment = paymentService.createPayment(order);
        assertThat(payment.status()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("Должен отклонить оплату при превышении лимита")
    void shouldCreateFailedPayment() {
        CustomerOrder order = new CustomerOrder(
                1L,
                "Иван",
                "Ноутбук",
                1,
                BigDecimal.valueOf(150_000),
                "Москва"
        );
        Payment payment = paymentService.createPayment(order);
        assertThat(payment.status()).isEqualTo(PaymentStatus.FAILED);
    }
}