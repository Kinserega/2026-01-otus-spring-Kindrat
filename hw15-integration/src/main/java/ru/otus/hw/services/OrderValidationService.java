package ru.otus.hw.services;

import ru.otus.hw.domain.CustomerOrder;

public interface OrderValidationService {

    CustomerOrder validate(CustomerOrder order);
}