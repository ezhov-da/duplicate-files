package ru.ezhov.duplicate.files.stamp.generator.model.service;

public class StampGeneratorException extends Exception {
    StampGeneratorException() {
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
