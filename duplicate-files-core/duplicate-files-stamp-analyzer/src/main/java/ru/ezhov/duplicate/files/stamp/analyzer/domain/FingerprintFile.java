package ru.ezhov.duplicate.files.stamp.analyzer.domain;

import java.io.File;

public interface FingerprintFile {
    String fingerprint();

    File file();
}
