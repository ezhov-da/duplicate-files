package ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.service;

import ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.domain.FileStamp;

public interface FileBruteForceListener {
    void action(FileStamp fileStamp);
}
