package ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.repository;

import org.junit.Test;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FingerprintFile;

import java.io.File;
import java.util.List;

public class Md5SumBashUtilRepositoryTest {

    @Test
    public void all() throws FingerprintRepositoryException {
        Md5SumBashUtilRepository md5SumBashUtilRepository = new Md5SumBashUtilRepository(new File("D:/duplicate.txt"));

        final List<FingerprintFile> all = md5SumBashUtilRepository.all();

        System.out.println(all.size());
    }

}