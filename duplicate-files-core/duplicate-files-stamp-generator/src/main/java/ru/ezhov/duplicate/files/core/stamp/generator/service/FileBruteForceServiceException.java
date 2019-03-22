package ru.ezhov.duplicate.files.core.stamp.generator.service;

public class FileBruteForceServiceException extends Exception {
    public FileBruteForceServiceException() {
    }

    public FileBruteForceServiceException(String message) {
        super(message);
    }

    public FileBruteForceServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileBruteForceServiceException(Throwable cause) {
        super(cause);
    }
}
