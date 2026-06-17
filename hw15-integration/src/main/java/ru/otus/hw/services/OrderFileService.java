package ru.otus.hw.services;

import ru.otus.hw.domain.CustomerOrder;

import java.util.List;

public interface OrderFileService {

    List<CustomerOrder> loadOrders();
}