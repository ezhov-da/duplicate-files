package ru.ezhov.duplicate.files;

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
