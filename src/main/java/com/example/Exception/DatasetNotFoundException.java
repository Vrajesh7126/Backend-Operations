package com.example.Exception;

/**
 * Custom exception thrown when a dataset is not found in the repository.
 * Extends RuntimeException to allow unchecked exception handling.
 */
public class DatasetNotFoundException extends RuntimeException {

    /**
     * Constructs a new DatasetNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public DatasetNotFoundException(String message) {
        super(message);
    }
}
