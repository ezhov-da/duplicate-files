package ru.ezhov.duplicate.files.core.stamp.analyzer.service;

public class DuplicateFilesAnalyserServiceException extends Exception {
    public DuplicateFilesAnalyserServiceException() {
    }

    public DuplicateFilesAnalyserServiceException(String message) {
        super(message);
    }

    public DuplicateFilesAnalyserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateFilesAnalyserServiceException(Throwable cause) {
        super(cause);
    }
}
