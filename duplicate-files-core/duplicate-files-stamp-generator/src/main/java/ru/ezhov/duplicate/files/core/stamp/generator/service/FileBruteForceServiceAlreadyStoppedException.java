package ru.ezhov.duplicate.files.core.stamp.generator.service;

public class FileBruteForceServiceAlreadyStoppedException extends Exception {
    public FileBruteForceServiceAlreadyStoppedException() {
    }

    public FileBruteForceServiceAlreadyStoppedException(String message) {
        super(message);
    }

    public FileBruteForceServiceAlreadyStoppedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileBruteForceServiceAlreadyStoppedException(Throwable cause) {
        super(cause);
    }
}
