package ru.ezhov.duplicate.files.core.stamp.generator.service;

import java.io.File;

public class FileStamp {
    private String stamp;
    private File file;

    public FileStamp(String stamp, File file) {
        this.stamp = stamp;
        this.file = file;
    }

    public String getStamp() {
        return stamp;
    }

    public File getFile() {
        return file;
    }
}
