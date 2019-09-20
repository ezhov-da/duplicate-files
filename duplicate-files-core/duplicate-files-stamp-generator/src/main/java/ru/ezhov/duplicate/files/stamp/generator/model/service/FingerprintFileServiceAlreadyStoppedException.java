package ru.ezhov.duplicate.files.stamp.generator.model.service;

public class FingerprintFileServiceAlreadyStoppedException extends Exception {
    FingerprintFileServiceAlreadyStoppedException() {
    }

    FingerprintFileServiceAlreadyStoppedException(String message) {
        super(message);
    }

    FingerprintFileServiceAlreadyStoppedException(String message, Throwable cause) {
        super(message, cause);
    }

    FingerprintFileServiceAlreadyStoppedException(Throwable cause) {
        super(cause);
    }
}
