package ru.ezhov.duplicate.files.stamp.analyzer.domain;

import ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.repository.FingerprintRepositoryException;

import java.util.List;

public interface FingerprintRepository {
    List<FingerprintFile> all() throws FingerprintRepositoryException;
}
