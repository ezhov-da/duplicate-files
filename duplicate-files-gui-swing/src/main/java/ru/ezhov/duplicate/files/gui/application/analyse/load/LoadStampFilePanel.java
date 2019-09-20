package ru.ezhov.duplicate.files.gui.application.analyse.load;

import ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.service.DuplicateFingerPrintFilesAnalyserServiceFactory;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFingerPrintFilesAnalyserService;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFingerprintFileAnalyserServiceException;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FingerprintFile;
import ru.ezhov.duplicate.files.stamp.generator.infrastructure.repository.FingerprintFileRepositoryFactory;
import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStamp;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepository;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepositoryException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class LoadStampFilePanel extends JPanel {
    private JTextField textFieldFileStampGenerator;
    private JButton buttonBrowseFileStampGenerator;
    private JButton buttonAnalyseStampGenerator;

    private Set<DuplicateAnalyseCompleteListener> duplicateAnalyseCompleteListeners = new HashSet();

    public LoadStampFilePanel() {
        setLayout(new BorderLayout());
        textFieldFileStampGenerator = new JTextField();
        buttonBrowseFileStampGenerator = new JButton("...");
        buttonAnalyseStampGenerator = new JButton("Анализировать");
        buttonBrowseFileStampGenerator.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getAbsolutePath().endsWith(".xml.dblfs");
                }

                @Override
                public String getDescription() {
                    return ".xml.dblfs";
                }
            });
            int action = fileChooser.showOpenDialog(LoadStampFilePanel.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() ->
                        textFieldFileStampGenerator.setText(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        });
        buttonAnalyseStampGenerator.addActionListener(e -> {
            DuplicateFingerPrintFilesAnalyserService duplicateFilesAnalyserService = DuplicateFingerPrintFilesAnalyserServiceFactory.newInstance();
            FingerprintFileRepository fingerprintFileRepository = FingerprintFileRepositoryFactory.newInstance(new File(textFieldFileStampGenerator.getText()));
            try {
                List<FileStamp> all = fingerprintFileRepository.all();
                List<FingerprintFile> fingerprintFiles =
                        all.stream().map(fpf -> new FingerprintFile() {

                            @Override
                            public String fingerprint() {
                                return fpf.stamp();
                            }

                            @Override
                            public File file() {
                                return fpf.file();
                            }
                        }).collect(Collectors.toList());


                Map<DuplicateId, List<FilePath>> map = duplicateFilesAnalyserService.findDuplicate(fingerprintFiles);
                for (DuplicateAnalyseCompleteListener listener : duplicateAnalyseCompleteListeners) {
                    listener.complete(Collections.unmodifiableMap(map));
                }
            } catch (FingerprintFileRepositoryException e1) {
                e1.printStackTrace();
            }
        });
        JPanel panelBrowse = new JPanel(new BorderLayout());
        panelBrowse.add(textFieldFileStampGenerator, BorderLayout.CENTER);
        JPanel panelBrowseButtons = new JPanel(new BorderLayout());
        panelBrowseButtons.add(buttonBrowseFileStampGenerator, BorderLayout.WEST);
        panelBrowseButtons.add(buttonAnalyseStampGenerator, BorderLayout.CENTER);
        panelBrowse.add(panelBrowseButtons, BorderLayout.EAST);

        add(panelBrowse, BorderLayout.CENTER);
    }

    public void addCompleteListener(DuplicateAnalyseCompleteListener duplicateAnalyseCompleteListener) {
        duplicateAnalyseCompleteListeners.add(duplicateAnalyseCompleteListener);
    }
}