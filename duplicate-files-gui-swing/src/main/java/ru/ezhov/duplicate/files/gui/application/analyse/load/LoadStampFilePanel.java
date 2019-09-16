package ru.ezhov.duplicate.files.gui.application.analyse.load;

import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFilesAnalyserService;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateFilesAnalyserServiceException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;

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
            DuplicateFilesAnalyserService duplicateFilesAnalyserService = new DuplicateFilesAnalyserService();
            try {
                Map<DuplicateId, List<FilePath>> map = duplicateFilesAnalyserService.findDuplicate(new File(textFieldFileStampGenerator.getText()));
                for(DuplicateAnalyseCompleteListener listener : duplicateAnalyseCompleteListeners){
                    listener.complete(Collections.unmodifiableMap(map));
                }
            } catch (DuplicateFilesAnalyserServiceException e1) {
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