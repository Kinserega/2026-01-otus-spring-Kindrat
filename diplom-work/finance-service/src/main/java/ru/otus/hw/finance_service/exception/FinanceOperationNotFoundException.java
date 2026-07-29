package ru.otus.hw.finance_service.exception;

public class FinanceOperationNotFoundException extends RuntimeException {

    public FinanceOperationNotFoundException(String message) {
        super(message);
    }
}