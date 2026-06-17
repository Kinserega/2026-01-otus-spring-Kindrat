package ru.otus.hw.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.CustomerOrder;
import ru.otus.hw.services.OrderFileService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFileServiceImpl implements OrderFileService {

    private static final String ORDERS_FILE_NAME = "orders.json";

    private final ObjectMapper objectMapper;

    @Override
    public List<CustomerOrder> loadOrders() {
        try (InputStream inputStream = new ClassPathResource(ORDERS_FILE_NAME).getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException exception) {
            throw new IllegalStateException("Не удалось загрузить файл заказов", exception);
        }
    }
}