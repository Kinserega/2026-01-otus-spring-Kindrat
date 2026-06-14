package ru.otus.hw.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.services.DeliveryService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderIntegrationFlowTest {

    @Autowired
    private OrderGateway orderGateway;

    @MockitoSpyBean
    private DeliveryService deliveryService;

    @Test
    @DisplayName("Должен создать доставку для успешно оплаченного заказа")
    void shouldCreateDeliveryForSuccessfulPayment() {
        CustomerOrder order = new CustomerOrder(
                1L,
                "Иван",
                "Ноутбук",
                1,
                BigDecimal.valueOf(50_000),
                "Москва"
        );

        orderGateway.process(order);
        verify(deliveryService, timeout(1000))
                .createDelivery(any(), eq(order));
    }

    @Test
    @DisplayName("Не должен создавать доставку при отклонённой оплате")
    void shouldNotCreateDeliveryForFailedPayment() {

        CustomerOrder order = new CustomerOrder(
                1L,
                "Иван",
                "Ноутбук",
                1,
                BigDecimal.valueOf(150_000),
                "Москва"
        );

        orderGateway.process(order);
        verify(deliveryService, after(500).never())
                .createDelivery(any(), any());
    }
}