package ru.otus.hw.services;

import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.domain.Delivery;
import ru.otus.hw.domain.Payment;

public interface DeliveryService {

    Delivery createDelivery(Payment payment, CustomerOrder order);
}
