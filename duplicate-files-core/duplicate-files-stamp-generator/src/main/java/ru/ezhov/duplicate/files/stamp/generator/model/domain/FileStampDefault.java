package ru.ezhov.duplicate.files.stamp.generator.model.domain;

import java.io.File;

public class FileStampDefault implements FileStamp {
    private String stamp;
    private File file;

    public FileStampDefault(String stamp, File file) {
        this.stamp = stamp;
        this.file = file;
    }

    public String stamp() {
        return stamp;
    }

    public File file() {
        return file;
    }
}
