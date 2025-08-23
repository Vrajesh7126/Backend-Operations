package com.example.Exception;

/**
 * Exception thrown when an invalid or unsupported field is used
 * for operations such as sorting or grouping in the dataset.
 * This is a custom unchecked exception.
 */
public class InvalidFieldException extends RuntimeException {

    /**
     * Constructs a new InvalidFieldException with the specified detail message.
     *
     * @param message Detailed message explaining the reason for the exception.
     */
    public InvalidFieldException(String message) {
        super(message);
    }
}
