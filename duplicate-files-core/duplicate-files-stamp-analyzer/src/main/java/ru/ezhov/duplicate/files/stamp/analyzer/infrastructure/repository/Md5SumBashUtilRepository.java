package ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.repository;

import ru.ezhov.duplicate.files.stamp.analyzer.domain.FingerprintFile;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FingerprintRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Md5SumBashUtilRepository implements FingerprintRepository {

    private File file;

    public Md5SumBashUtilRepository(File file) {
        this.file = file;
    }

    @Override
    public List<FingerprintFile> all() throws FingerprintRepositoryException {
        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            List<FingerprintFile> files = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                final int index = line.indexOf('*');
                if (index == -1) {
                    throw new FingerprintRepositoryException("Error format '" + file.getAbsolutePath() + "'");

                }

                String sum = line.substring(0, index);
                String file = convertPath(line.substring(index + 1));

                files.add(new FingerprintFile() {
                    @Override
                    public String fingerprint() {
                        return sum;
                    }

                    @Override
                    public File file() {
                        return new File(file);
                    }
                });
            }

            return files;
        } catch (FileNotFoundException e) {
            throw new FingerprintRepositoryException("Exception with file '" + file.getAbsolutePath() + "'", e);
        }
    }

    private String convertPath(String path) {
        String begin = path.substring(0, 2);
        String another = path.substring(3);
        String disk = begin.substring(1);
        return disk.toUpperCase() + ":/" + another;
    }
}
