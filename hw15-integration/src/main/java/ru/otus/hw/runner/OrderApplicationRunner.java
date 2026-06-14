package ru.otus.hw.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.gateway.OrderGateway;
import ru.otus.hw.services.OrderFileService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApplicationRunner implements ApplicationRunner {

    private final OrderGateway orderGateway;

    private final OrderFileService orderFileService;

    @Override
    public void run(ApplicationArguments args) {
        orderFileService.loadOrders().forEach(this::processOrder);
    }

    private void processOrder(CustomerOrder order) {
        try {
            orderGateway.process(order);
        } catch (Exception exception) {
            log.error("Ошибка обработки заказа {}: {}", order.id(), exception.getMessage());
        }
    }
}