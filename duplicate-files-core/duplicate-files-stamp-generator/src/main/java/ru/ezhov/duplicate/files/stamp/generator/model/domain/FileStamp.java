package ru.ezhov.duplicate.files.stamp.generator.model.domain;

import java.io.File;

public interface FileStamp {
    String stamp();

    File file();
}
