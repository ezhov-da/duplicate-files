package ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.service;

public class FileBruteForceServiceException extends Exception {
    FileBruteForceServiceException() {
    }

    FileBruteForceServiceException(String message) {
        super(message);
    }

    FileBruteForceServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    FileBruteForceServiceException(Throwable cause) {
        super(cause);
    }
}
