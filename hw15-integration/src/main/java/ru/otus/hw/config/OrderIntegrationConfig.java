package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.domain.Payment;
import ru.otus.hw.enums.PaymentStatus;
import ru.otus.hw.services.DeliveryService;
import ru.otus.hw.services.OrderValidationService;
import ru.otus.hw.services.PaymentService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderIntegrationConfig {

    private static final String ORDER_HEADER = "customerOrder";

    private final OrderValidationService orderValidationService;

    private final PaymentService paymentService;

    private final DeliveryService deliveryService;

    @Bean
    public IntegrationFlow orderFlow() {
        return IntegrationFlow.from("customerOrderChannel")
                .handle(CustomerOrder.class, (order, headers) -> orderValidationService.validate(order))
                .log(message -> "Заказ прошёл валидацию: " + message.getPayload())
                .enrichHeaders(headers -> headers.headerFunction(ORDER_HEADER, Message::getPayload))
                .transform(CustomerOrder.class, paymentService::createPayment)
                .log(message -> "Создана оплата: " + message.getPayload())
                .route(Payment.class, Payment::status, mapping -> mapping
                        .subFlowMapping(PaymentStatus.SUCCESS, successPaymentFlow())
                        .subFlowMapping(PaymentStatus.FAILED, failedPaymentFlow()))
                .get();
    }

    private IntegrationFlow successPaymentFlow() {
        return flow -> flow
                .handle(Payment.class, (payment, headers) -> {
                    CustomerOrder order = (CustomerOrder) headers.get(ORDER_HEADER);
                    return deliveryService.createDelivery(payment, order);
                })
                .log(message -> "Доставка создана: " + message.getPayload());
    }

    private IntegrationFlow failedPaymentFlow() {
        return flow -> flow
                .handle(Payment.class, (payment, headers) -> {
                    log.warn("Оплата отклонена. Заказ не будет передан в доставку. Payment: {}", payment);
                    return null;
                });
    }
}