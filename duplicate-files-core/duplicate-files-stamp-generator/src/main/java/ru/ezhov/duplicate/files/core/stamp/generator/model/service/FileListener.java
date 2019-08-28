package ru.ezhov.duplicate.files.core.stamp.generator.model.service;

public interface FileListener {
    void stampOn(String absoluteFilePath);
}
