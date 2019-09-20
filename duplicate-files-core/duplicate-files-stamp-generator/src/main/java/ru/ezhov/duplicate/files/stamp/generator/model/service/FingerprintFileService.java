package ru.ezhov.duplicate.files.stamp.generator.model.service;

public interface FingerprintFileService {
    void start(FingerprintFileListener fingerprintFileListener) throws FingerprintFileServiceException;

    void stop() throws FingerprintFileServiceAlreadyStoppedException;
}
