package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.exceptions.OrderValidationException;
import ru.otus.hw.services.OrderValidationService;
import ru.otus.hw.services.impl.OrderValidationServiceImpl;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Тестирование сервиса валидации заказа")
class OrderValidationServiceImplTest {

    private final OrderValidationService validationService =
            new OrderValidationServiceImpl();

    @Test
    @DisplayName("Должен успешно провалидировать корректный заказ")
    void shouldValidateOrder() {
        CustomerOrder order = new CustomerOrder(
                1L,
                "Иван",
                "Ноутбук",
                1,
                BigDecimal.valueOf(50_000),
                "Москва"
        );
        CustomerOrder result = validationService.validate(order);
        assertThat(result).isEqualTo(order);
    }

    @Test
    @DisplayName("Должен выбросить исключение при количестве меньше либо равно нулю")
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        CustomerOrder order = new CustomerOrder(
                1L,
                "Иван",
                "Ноутбук",
                0,
                BigDecimal.valueOf(50_000),
                "Москва"
        );
        assertThatThrownBy(() -> validationService.validate(order))
                .isInstanceOf(OrderValidationException.class)
                .hasMessageContaining("Количество товара");
    }
}