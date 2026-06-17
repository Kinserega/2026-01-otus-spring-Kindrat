package ru.otus.hw.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.CustomerOrder;

@MessagingGateway
public interface OrderGateway {

    @Gateway(requestChannel = "customerOrderChannel")
    void process(CustomerOrder order);
}
