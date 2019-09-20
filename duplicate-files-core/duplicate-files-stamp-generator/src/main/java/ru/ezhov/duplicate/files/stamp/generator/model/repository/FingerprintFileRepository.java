package ru.ezhov.duplicate.files.stamp.generator.model.repository;

import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStamp;

import java.util.List;

public interface FingerprintFileRepository {
    void save(List<FileStamp> fileStampDefaults) throws FingerprintFileRepositoryException;

    List<FileStamp> all() throws FingerprintFileRepositoryException;
}
