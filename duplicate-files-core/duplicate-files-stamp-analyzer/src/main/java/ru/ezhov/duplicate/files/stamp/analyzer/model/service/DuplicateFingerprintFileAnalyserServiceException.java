package ru.ezhov.duplicate.files.stamp.analyzer.model.service;

public class DuplicateFingerprintFileAnalyserServiceException extends Exception {
    public DuplicateFingerprintFileAnalyserServiceException() {
    }

    public DuplicateFingerprintFileAnalyserServiceException(String message) {
        super(message);
    }

    public DuplicateFingerprintFileAnalyserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateFingerprintFileAnalyserServiceException(Throwable cause) {
        super(cause);
    }
}
