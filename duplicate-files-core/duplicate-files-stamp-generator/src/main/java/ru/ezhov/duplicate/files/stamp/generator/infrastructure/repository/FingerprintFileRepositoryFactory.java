package ru.ezhov.duplicate.files.stamp.generator.infrastructure.repository;

import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepository;

import java.io.File;

public abstract class FingerprintFileRepositoryFactory {
    public static FingerprintFileRepository newInstance(File store) {
        return new XmlFingerprintFileRepository(store);
    }
}
