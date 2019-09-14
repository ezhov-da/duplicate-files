package ru.ezhov.duplicate.files.stamp.generator.model.service;

public class FileBruteForceServiceAlreadyStoppedException extends Exception {
    FileBruteForceServiceAlreadyStoppedException() {
    }

    FileBruteForceServiceAlreadyStoppedException(String message) {
        super(message);
    }

    FileBruteForceServiceAlreadyStoppedException(String message, Throwable cause) {
        super(message, cause);
    }

    FileBruteForceServiceAlreadyStoppedException(Throwable cause) {
        super(cause);
    }
}
