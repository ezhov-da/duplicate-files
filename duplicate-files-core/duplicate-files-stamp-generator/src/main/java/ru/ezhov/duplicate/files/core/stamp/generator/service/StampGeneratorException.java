package ru.ezhov.duplicate.files.core.stamp.generator.service;

public class StampGeneratorException extends Exception {
    public StampGeneratorException() {
    }

    public StampGeneratorException(String message) {
        super(message);
    }

    public StampGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StampGeneratorException(Throwable cause) {
        super(cause);
    }
}
