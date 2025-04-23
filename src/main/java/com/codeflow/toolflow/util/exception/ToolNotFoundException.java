package com.codeflow.toolflow.util.exception;

/**
 * Exception thrown when an entity is not found in the database.
 */
public class ToolNotFoundException extends RuntimeException {

    /**
     * Constructs a new EntityNotFoundException with a custom message.
     *
     * @param message the detail message explaining the exception.
     */
    public ToolNotFoundException(String message) {
        super(message);
    }
}
