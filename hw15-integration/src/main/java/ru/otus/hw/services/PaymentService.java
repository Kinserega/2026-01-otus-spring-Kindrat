package ru.otus.hw.services;

import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.domain.Payment;

public interface PaymentService {

    Payment createPayment(CustomerOrder order);
}