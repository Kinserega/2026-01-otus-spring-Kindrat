package ru.otus.hw.services.impl;

import org.springframework.stereotype.Service;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.domain.Payment;
import ru.otus.hw.enums.PaymentStatus;
import ru.otus.hw.services.PaymentService;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal MAX_ALLOWED_PAYMENT_AMOUNT = BigDecimal.valueOf(100_000);

    @Override
    public Payment createPayment(CustomerOrder order) {
        PaymentStatus status = resolvePaymentStatus(order.totalAmount());
        return new Payment(order.id(), order.totalAmount(), status);
    }

    private PaymentStatus resolvePaymentStatus(BigDecimal amount) {
        if (amount.compareTo(MAX_ALLOWED_PAYMENT_AMOUNT) > 0) {
            return PaymentStatus.FAILED;
        }
        return PaymentStatus.SUCCESS;
    }
}