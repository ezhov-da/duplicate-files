package ru.ezhov.duplicate.files.stamp.generator.model.service;

import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStampDefault;

public interface FingerprintFileListener {
    void stampedOn(FileStampDefault fileStampDefault);
}
