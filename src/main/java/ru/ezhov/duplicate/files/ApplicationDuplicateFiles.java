package ru.ezhov.duplicate.files;

import ru.ezhov.duplicate.files.core.AnalyseMd5DuplicateFilesXml;
import ru.ezhov.duplicate.files.core.CreateMd5DuplicateFilesXml;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationDuplicateFiles {
    private static final Logger LOG = Logger.getLogger(ApplicationDuplicateFiles.class.getName());

    public static void main(String[] args) {
        if ("create".equals(args[0])) {
            create();
        } else if ("analyse".equals(args[0])) {
            analyse();
        }
    }

    private static void create() {
        String fileRoot = System.getProperty("file.path.root");
        String fileReport = System.getProperty("file.path.report");
        File root = new File(fileRoot);
        File xmlFileReport = new File(fileReport);
        CreateMd5DuplicateFilesXml createMd5DuplicateFilesXml = new CreateMd5DuplicateFilesXml();
        try {
            createMd5DuplicateFilesXml.create(root, xmlFileReport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void analyse() {
        String fileRootPath = System.getProperty("file.path.root");
        String xmlFileReportPath = System.getProperty("file.path.report");
        String htmlFileReportPath = System.getProperty("file.path.analyse.html.report");
        File root = new File(fileRootPath);
        File xmlFileReport = new File(xmlFileReportPath);
        File htmlFileReport = new File(htmlFileReportPath);
        AnalyseMd5DuplicateFilesXml analyseMd5DuplicateFilesXml = new AnalyseMd5DuplicateFilesXml();
        try {
            Map<String, List<String>> map = analyseMd5DuplicateFilesXml.findDuplicate(xmlFileReport);
            toHtmlReport(root, htmlFileReport, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void toHtmlReport(File root, File htmlFileReport, Map<String, List<String>> map) throws Exception {
        StringBuilder sbDuplicates = new StringBuilder();
        AtomicInteger counterDuplicate = new AtomicInteger();
        map.forEach((md5, paths) -> {
            if (paths.size() > 1) {
                sbDuplicates.append("<p>").append(md5).append(". повторов: ").append(paths.size()).append("</p>");
                paths.forEach(v ->
                {
                    File file = new File(v);
                    sbDuplicates
                            .append("<ul><li>")
                            .append(v)
                            .append(" <a href=\"file:///")
                            .append(v)
                            .append("\">")
                            .append(file.getName())
                            .append("</a></li></ul>");
                    counterDuplicate.getAndIncrement();
                });
            }
        });

        String begin = "<html><head><meta charset=\"utf-8\"></head><body><h2>Корневой каталог: " + root.getAbsolutePath() + ". Повторов: " + counterDuplicate.intValue() + "</h2>";
        String end = "</body></html>";
        String resultReport = begin + sbDuplicates.toString() + end;
        Files.write(htmlFileReport.toPath(), resultReport.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        LOG.log(Level.INFO, "html отчет сформирован по пути ''{0}''", htmlFileReport.getAbsolutePath());
    }
}
