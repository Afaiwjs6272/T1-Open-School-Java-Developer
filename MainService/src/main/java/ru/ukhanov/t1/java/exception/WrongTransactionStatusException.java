package ru.ukhanov.t1.java.exception;

public class WrongTransactionStatusException extends RuntimeException {
    public WrongTransactionStatusException(String message) {
        super(message);
    }
}
