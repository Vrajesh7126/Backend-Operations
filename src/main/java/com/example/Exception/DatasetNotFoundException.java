package com.example.Exception;

public class DatasetNotFoundException extends RuntimeException {
    public DatasetNotFoundException(String message) {
        super(message);
    }
}
