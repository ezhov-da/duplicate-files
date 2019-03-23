package ru.ezhov.duplicate.files.gui.analyse;

import ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.analyzer.service.DuplicateFilesAnalyserService;
import ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.analyzer.service.DuplicateFilesAnalyserServiceException;
import ru.ezhov.duplicate.files.gui.delete.queue.DuplicateFilesToRemovePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalysePanel extends JPanel {

    private JTextField textFieldFileStampGenerator;
    private JButton buttonBrowseFileStampGenerator;
    private JButton buttonAnalyseStampGenerator;
    private JPanel panelMock;
    private JPanel panelAnalyseResult;

    private List<MarkToDeleteFileListener> markToDeleteFileListeners = new ArrayList<>();

    public AnalysePanel(DuplicateFilesToRemovePanel duplicateFilesToRemovePanel) throws Exception {
        init();
    }

    public void addMarkToDeleteFileListener(MarkToDeleteFileListener markToDeleteFileListener) {
        markToDeleteFileListeners.add(markToDeleteFileListener);
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Анализ отпечатков")
        ));
        textFieldFileStampGenerator = new JTextField("D:/duplicate-files-md5.xml");
        buttonBrowseFileStampGenerator = new JButton("...");
        buttonAnalyseStampGenerator = new JButton("Анализировать");

        buttonAnalyseStampGenerator.addActionListener(e -> {
            DuplicateFilesAnalyserService duplicateFilesAnalyserService = new DuplicateFilesAnalyserService();
            try {
                Map<String, List<String>> map = duplicateFilesAnalyserService.findDuplicate(new File(textFieldFileStampGenerator.getText()));
                AnalyseResultTreeTablePanel newPanelAnalyseResult = new AnalyseResultTreeTablePanel(map);

                for (MarkToDeleteFileListener markToDeleteFileListener : markToDeleteFileListeners) {
                    newPanelAnalyseResult.addMarkToDeleteFileListener(markToDeleteFileListener);
                }

                if (panelMock != null) {
                    SwingUtilities.invokeLater(() -> {
                        remove(panelMock);
                        panelMock = null;
                    });
                }
                if (panelAnalyseResult != null) {
                    SwingUtilities.invokeLater(() -> {
                        remove(panelAnalyseResult);
                        panelAnalyseResult = newPanelAnalyseResult;
                        add(panelAnalyseResult, BorderLayout.CENTER);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        panelAnalyseResult = newPanelAnalyseResult;
                        add(panelAnalyseResult, BorderLayout.CENTER);
                    });
                }
                SwingUtilities.invokeLater(() -> {
                    revalidate();
                    repaint();
                });
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

        panelMock = new JPanel(new BorderLayout());
        panelMock.add(new JLabel("Запустите анализ файла на дубликаты"), BorderLayout.CENTER);

        add(panelBrowse, BorderLayout.NORTH);
        add(panelMock, BorderLayout.CENTER);
    }
}
