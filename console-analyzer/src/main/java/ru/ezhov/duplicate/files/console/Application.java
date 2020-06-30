package ru.ezhov.duplicate.files.console;

import ru.ezhov.duplicate.files.stamp.generator.infrastructure.repository.FingerprintFileRepositoryFactory;
import ru.ezhov.duplicate.files.stamp.generator.infrastructure.service.FingerprintFileServiceFactory;
import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStamp;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepository;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepositoryException;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileService;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileServiceException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Application {
    public static void main(String[] args) {
        String rootDirectory = args[0];
        String reportFile = args[1];

        try {
            final FingerprintFileService fingerprintFileService = FingerprintFileServiceFactory.newInstance(new File(rootDirectory));
            List<FileStamp> fileStamps = new ArrayList<>();

            System.out.printf("Start with root directory '%s' and report file '%s'\n", rootDirectory, reportFile);

            AtomicInteger atomicInteger = new AtomicInteger(1);

            fingerprintFileService.start(fileStamp -> {
                fileStamps.add(fileStamp);
                final int current = atomicInteger.getAndIncrement();
                System.out.printf("Processed: %s. File '%s'. Stamp '%s'\r", current, fileStamp.file().getAbsolutePath(), fileStamp.stamp());
            });

            System.out.println();

            final FingerprintFileRepository fingerprintFileRepository = FingerprintFileRepositoryFactory.newInstance(new File(reportFile));

            fingerprintFileRepository.save(fileStamps);

            System.out.println("Completed and saved to '" + reportFile + "'");
        } catch (FingerprintFileServiceException | FingerprintFileRepositoryException e) {
            e.printStackTrace();
        }
    }
}
