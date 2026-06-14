package ru.otus.hw.services.impl;

import org.springframework.stereotype.Service;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.domain.Delivery;
import ru.otus.hw.domain.Payment;
import ru.otus.hw.enums.DeliveryStatus;
import ru.otus.hw.services.DeliveryService;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private static final String TRACKING_NUMBER_TEMPLATE = "DLV-%06d";

    @Override
    public Delivery createDelivery(Payment payment, CustomerOrder order) {
        return new Delivery(
                payment.orderId(),
                order.deliveryAddress(),
                buildTrackingNumber(payment.orderId()),
                DeliveryStatus.CREATED
        );
    }

    private String buildTrackingNumber(Long orderId) {
        return TRACKING_NUMBER_TEMPLATE.formatted(orderId);
    }
}