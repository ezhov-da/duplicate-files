package ru.ezhov.duplicate.files.stamp.generator.model.repository;

public class FingerprintFileRepositoryException extends Exception {
    public FingerprintFileRepositoryException() {
    }

    public FingerprintFileRepositoryException(String message) {
        super(message);
    }

    public FingerprintFileRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FingerprintFileRepositoryException(Throwable cause) {
        super(cause);
    }

    public FingerprintFileRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
