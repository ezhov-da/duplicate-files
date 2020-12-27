package ru.ezhov.duplicate.files;

import ru.ezhov.duplicate.files.stamp.analyzer.domain.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FingerprintRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.repository.Md5SumBashUtilRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.service.DuplicateFingerPrintFilesAnalyserServiceFactory;

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

    private static void toHtmlReport(File root, File htmlFileReport, Map<DuplicateId, List<FilePath>> map) throws Exception {
        StringBuilder sbDuplicates = new StringBuilder();
        AtomicInteger counterDuplicate = new AtomicInteger();
        map.forEach((md5, paths) -> {
            if (paths.size() > 1) {
                sbDuplicates.append("<div class=\"block\"><p>").append(md5.id()).append(". repetitions: ").append(paths.size()).append("</p>");
                sbDuplicates.append("<ul>");
                final String[] pathToImage = {""};
                paths.forEach(v ->
                {
                    File file = new File(v.path());
                    sbDuplicates
                            .append("<ul><li>")
                            .append(v)
                            .append(" <a href=\"file:///")
                            .append(v)
                            .append("\">")
                            .append(file.getName())
                            .append("</a></li>");

                    pathToImage[0] = v.path();
                    counterDuplicate.getAndIncrement();
                });
                sbDuplicates.append("</ul></div>");

                sbDuplicates.append("<figure class=\"image is-128x128\">" +
                        "  <img src=\"file:///" + pathToImage[0] + "\">" +
                        "</figure>");
                sbDuplicates.append("</div>");
            }
        });

        String begin = "<html>" +
                "<head>" +
                "<meta charset=\"utf-8\">" +
                "    <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.9.1/css/bulma.min.css\">\n" +
                "    <script defer src=\"https://use.fontawesome.com/releases/v5.14.0/js/all.js\"></script>" +
                "</head>" +
                "<body><h2>Root directory: " + root.getAbsolutePath() + ". Replays: " + counterDuplicate.intValue() + "</h2><br/>";
        String end = "</body></html>";
        String resultReport = begin + sbDuplicates.toString() + end;
        Files.write(htmlFileReport.toPath(), resultReport.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        LOG.log(Level.INFO, "html report generated along the path ''{0}''", htmlFileReport.getAbsolutePath());
    }

    public static void main(String[] args) {
        try {
            FingerprintRepository fingerprintRepository = new Md5SumBashUtilRepository(new File("D:/duplicate.txt"));
            toHtmlReport(
                    new File("D:\\redmi3s-20201221-work"),
                    new File("D:/html_duplicate.html"),
                    DuplicateFingerPrintFilesAnalyserServiceFactory.newInstance().findDuplicate(
                            fingerprintRepository.all()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
