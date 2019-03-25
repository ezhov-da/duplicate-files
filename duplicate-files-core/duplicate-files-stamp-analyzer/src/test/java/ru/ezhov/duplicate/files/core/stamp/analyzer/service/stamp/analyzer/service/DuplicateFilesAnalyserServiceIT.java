package ru.ezhov.duplicate.files.core.stamp.analyzer.service.stamp.analyzer.service;

import org.junit.Test;
import ru.ezhov.duplicate.files.core.stamp.analyzer.service.DuplicateFilesAnalyserService;
import ru.ezhov.duplicate.files.core.stamp.analyzer.service.DuplicateFilesAnalyserServiceException;

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