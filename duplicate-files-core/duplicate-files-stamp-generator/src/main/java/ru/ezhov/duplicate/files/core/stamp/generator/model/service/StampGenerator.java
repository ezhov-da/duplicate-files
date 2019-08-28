package ru.ezhov.duplicate.files.core.stamp.generator.model.service;

import java.io.File;

public interface StampGenerator {
    String generate(File file) throws StampGeneratorException;
}
