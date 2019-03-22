package ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.analyzer.service;

import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

public class DuplicateFilesAnalyserServiceIT {
    @Test
    public void findDuplicateWithoutError() throws DuplicateFilesAnalyserServiceException {
        DuplicateFilesAnalyserService duplicateFilesAnalyserService = new DuplicateFilesAnalyserService();
        Map<String, List<String>> duplicate = duplicateFilesAnalyserService.findDuplicate(new File("D:/duplicate-files-md5.xml"));

        System.out.println(duplicate.size());
    }

}