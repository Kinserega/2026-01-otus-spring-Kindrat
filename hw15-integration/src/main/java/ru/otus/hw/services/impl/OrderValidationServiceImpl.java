package ru.otus.hw.services.impl;

import org.springframework.stereotype.Service;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.exceptions.OrderValidationException;
import ru.otus.hw.services.OrderValidationService;

import java.math.BigDecimal;

@Service
public class OrderValidationServiceImpl implements OrderValidationService {

    @Override
    public CustomerOrder validate(CustomerOrder order) {
        if (order == null) {
            throw new OrderValidationException("Заказ не должен быть null");
        }

        validateOrderId(order);
        validateCustomerName(order);
        validateProductName(order);
        validateQuantity(order);
        validateTotalAmount(order);
        validateDeliveryAddress(order);

        return order;
    }

    private void validateOrderId(CustomerOrder order) {
        if (order.id() == null) {
            throw new OrderValidationException("Идентификатор заказа не должен быть null");
        }
    }

    private void validateCustomerName(CustomerOrder order) {
        if (isBlank(order.customerName())) {
            throw new OrderValidationException("Имя клиента не должно быть пустым");
        }
    }

    private void validateProductName(CustomerOrder order) {
        if (isBlank(order.productName())) {
            throw new OrderValidationException("Наименование товара не должно быть пустым");
        }
    }

    private void validateQuantity(CustomerOrder order) {
        if (order.quantity() <= 0) {
            throw new OrderValidationException("Количество товара должно быть больше нуля");
        }
    }

    private void validateTotalAmount(CustomerOrder order) {
        if (order.totalAmount() == null || order.totalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderValidationException("Сумма заказа должна быть больше нуля");
        }
    }

    private void validateDeliveryAddress(CustomerOrder order) {
        if (isBlank(order.deliveryAddress())) {
            throw new OrderValidationException("Адрес доставки не должен быть пустым");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}