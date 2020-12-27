package ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.repository;

public class FingerprintRepositoryException extends Exception {
    public FingerprintRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FingerprintRepositoryException(String message) {
        super(message);
    }
}
