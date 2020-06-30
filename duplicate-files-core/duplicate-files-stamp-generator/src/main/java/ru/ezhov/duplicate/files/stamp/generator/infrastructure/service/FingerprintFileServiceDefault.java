package ru.ezhov.duplicate.files.stamp.generator.infrastructure.service;

import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStampDefault;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileListener;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileService;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileServiceException;
import ru.ezhov.duplicate.files.stamp.generator.model.service.StampGenerator;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

class FingerprintFileServiceDefault implements FingerprintFileService {
    private static final Logger LOG = Logger.getLogger(FingerprintFileServiceDefault.class.getName());
    private final File root;
    private final StampGenerator stampGenerator;
    private final AtomicBoolean runningBruteForce = new AtomicBoolean();

    FingerprintFileServiceDefault(File root, StampGenerator stampGenerator) {
        this.root = root;
        this.stampGenerator = stampGenerator;
    }

    public void start(FingerprintFileListener fingerprintFileListener) throws FingerprintFileServiceException {
        runningBruteForce.set(true);
        try {
            fillMd5FilesRecursive(root, stampGenerator, fingerprintFileListener, null);
        } catch (Exception e) {
            throw new FingerprintFileServiceException(e);
        } finally {
            runningBruteForce.set(false);
        }
    }

    public void stop() {
        runningBruteForce.set(false);
    }

    private void fillMd5FilesRecursive(File file, StampGenerator stampGenerator, FingerprintFileListener fingerprintFileListener, FileFilter fileFilter) throws Exception {
        if (!runningBruteForce.get()) {
            return;
        }
        if (file.isDirectory()) {
            LOG.log(Level.CONFIG, "folder processing ''{0}''", file.getAbsolutePath());
            File[] files;
            if (fileFilter == null) {
                files = file.listFiles();
            } else {
                files = file.listFiles(fileFilter);
            }
            if (files != null) {
                for (File f : files) {
                    fillMd5FilesRecursive(f, stampGenerator, fingerprintFileListener, fileFilter);
                }
            }
        } else {
            long freeMemoryBefore = Runtime.getRuntime().freeMemory();
            long totalMemoryBefore = Runtime.getRuntime().totalMemory();
            String stamp = stampGenerator.generate(file);
            long freeMemoryAfter = Runtime.getRuntime().freeMemory();
            long totalMemoryAfter = Runtime.getRuntime().totalMemory();

            LOG.log(Level.CONFIG, "method=fillMd5FilesRecursive stampedOn=\"memory\" freeMemoryBefore={0} freeMemoryAfter={1} totalMemoryBefore={2} totalMemoryAfter={3} file={4}", new Object[]{freeMemoryBefore, freeMemoryAfter, totalMemoryBefore, totalMemoryAfter, file});

            fingerprintFileListener.stampedOn(new FileStampDefault(stamp, file));
        }
    }
}
