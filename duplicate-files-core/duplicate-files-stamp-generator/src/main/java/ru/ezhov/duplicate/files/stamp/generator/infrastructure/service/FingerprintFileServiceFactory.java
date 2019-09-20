package ru.ezhov.duplicate.files.stamp.generator.infrastructure.service;

import ru.ezhov.duplicate.files.stamp.generator.infrastructure.generator.Md5StampGenerator;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileService;

import java.io.File;

public abstract class FingerprintFileServiceFactory {
    public static FingerprintFileService newInstance(File root) {
        return new FingerprintFileServiceDefault(root, new Md5StampGenerator());
    }
}
