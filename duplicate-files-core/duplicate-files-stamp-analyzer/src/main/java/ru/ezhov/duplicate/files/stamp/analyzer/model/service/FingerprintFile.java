package ru.ezhov.duplicate.files.stamp.analyzer.model.service;

import java.io.File;

public interface FingerprintFile {
    String fingerprint();

    File file();
}
