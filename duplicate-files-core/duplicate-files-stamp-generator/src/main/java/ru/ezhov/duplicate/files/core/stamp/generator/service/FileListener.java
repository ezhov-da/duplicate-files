package ru.ezhov.duplicate.files.core.stamp.generator.service;

import java.io.File;

public interface FileListener {
    void stampOn(String absoluteFilePath);
}
