package ru.ezhov.duplicate.files.gui.application.analyse.load;

import ru.ezhov.duplicate.files.stamp.analyzer.infrastructure.service.DuplicateFingerPrintFilesAnalyserServiceFactory;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.DuplicateFingerPrintFilesAnalyserService;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FingerprintFile;
import ru.ezhov.duplicate.files.stamp.generator.infrastructure.repository.FingerprintFileRepositoryFactory;
import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStamp;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepository;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepositoryException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadStampFilePanel extends JPanel {
    private final JTextField textFieldFileStampGenerator;

    private final Set<DuplicateAnalyseCompleteListener> duplicateAnalyseCompleteListeners = new HashSet<>();

    public LoadStampFilePanel() {
        setLayout(new BorderLayout());
        textFieldFileStampGenerator = new JTextField();
        JButton buttonBrowseFileStampGenerator = new JButton("...");
        JButton buttonAnalyseStampGenerator = new JButton("Analyze");
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