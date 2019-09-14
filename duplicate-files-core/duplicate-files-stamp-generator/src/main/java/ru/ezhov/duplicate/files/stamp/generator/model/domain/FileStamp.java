package ru.ezhov.duplicate.files.stamp.generator.model.domain;

import java.io.File;

public class FileStamp {
    private String stamp;
    private File file;

    public FileStamp(String stamp, File file) {
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
