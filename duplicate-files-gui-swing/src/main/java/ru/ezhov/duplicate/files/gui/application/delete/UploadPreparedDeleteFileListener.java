package ru.ezhov.duplicate.files.gui.application.delete;

import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;

import java.util.List;

public interface UploadPreparedDeleteFileListener {
    void upload(List<FilePath> filePaths);
}
