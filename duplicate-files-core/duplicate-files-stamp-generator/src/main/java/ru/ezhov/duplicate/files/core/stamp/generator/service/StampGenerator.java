package ru.ezhov.duplicate.files.core.stamp.generator.service;

import java.io.File;

public interface StampGenerator {
    String generate(File file) throws StampGeneratorException;
}
