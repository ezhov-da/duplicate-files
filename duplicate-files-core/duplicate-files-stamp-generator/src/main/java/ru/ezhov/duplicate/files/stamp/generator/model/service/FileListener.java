package ru.ezhov.duplicate.files.stamp.generator.model.service;

public interface FileListener {
    void stampOn(String absoluteFilePath);
}
