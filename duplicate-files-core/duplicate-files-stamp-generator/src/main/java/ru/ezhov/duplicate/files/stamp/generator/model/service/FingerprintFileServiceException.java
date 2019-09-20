package ru.ezhov.duplicate.files.stamp.generator.model.service;

public class FingerprintFileServiceException extends Exception {
    public FingerprintFileServiceException() {
    }

    public FingerprintFileServiceException(String message) {
        super(message);
    }

    public FingerprintFileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FingerprintFileServiceException(Throwable cause) {
        super(cause);
    }
}
