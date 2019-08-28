package ru.ezhov.duplicate.files.core.stamp.generator.model.service;

import ru.ezhov.duplicate.files.core.stamp.generator.model.domain.FileStamp;

public interface FileBruteForceListener {
    void action(FileStamp fileStamp);
}
