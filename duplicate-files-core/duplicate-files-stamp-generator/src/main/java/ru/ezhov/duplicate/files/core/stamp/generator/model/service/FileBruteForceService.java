package ru.ezhov.duplicate.files.core.stamp.generator.model.service;

import ru.ezhov.duplicate.files.core.stamp.generator.model.domain.FileStamp;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileBruteForceService {
    private static final Logger LOG = Logger.getLogger(FileBruteForceService.class.getName());
    private File rootBrutForce;
    private StampGenerator stampGenerator;
    private AtomicBoolean runningBruteForce = new AtomicBoolean();

    public FileBruteForceService(File rootBrutForce, StampGenerator stampGenerator) {
        this.rootBrutForce = rootBrutForce;
        this.stampGenerator = stampGenerator;
    }

    public void bruteForceStart(FileBruteForceListener fileBruteForceListener) throws FileBruteForceServiceException {
        runningBruteForce.set(true);
        try {
            fillMd5FilesRecursive(rootBrutForce, stampGenerator, fileBruteForceListener);
        } catch (Exception e) {
            throw new FileBruteForceServiceException(e);
        } finally {
            runningBruteForce.set(false);
        }

    }

    public void stopBruteForce() throws FileBruteForceServiceAlreadyStoppedException {
        runningBruteForce.set(false);
    }

    private void fillMd5FilesRecursive(File file, StampGenerator stampGenerator, FileBruteForceListener fileBruteForceListener) throws Exception {
        if (!runningBruteForce.get()) {
            return;
        }
        if (file.isDirectory()) {
            LOG.log(Level.CONFIG, "обработка папки ''{0}''", file.getAbsolutePath());
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    fillMd5FilesRecursive(f, stampGenerator, fileBruteForceListener);
                }
            }
        } else {
            String stamp = stampGenerator.generate(file);
            fileBruteForceListener.action(new FileStamp(stamp, file));
        }
    }
}
