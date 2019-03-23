package ru.ezhov.duplicate.files.gui;

import ru.ezhov.duplicate.files.gui.analyse.AnalysePanel;
import ru.ezhov.duplicate.files.gui.delete.queue.DuplicateFilesToRemovePanel;
import ru.ezhov.duplicate.files.gui.stamp.StampPanel;

import javax.swing.*;
import java.awt.*;

public class DuplicateFilesGuiApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }
            try {
                JFrame frame = new JFrame("Дубликаты");
                frame.setIconImage(new ImageIcon(DuplicateFilesGuiApplication.class.getResource("/duplicate_16x16.png")).getImage());

                StampPanel stampPanel = new StampPanel();
                DuplicateFilesToRemovePanel duplicateFilesToRemovePanel = new DuplicateFilesToRemovePanel();
                AnalysePanel analysePanel = new AnalysePanel(duplicateFilesToRemovePanel);
                analysePanel.addMarkToDeleteFileListener(duplicateFilesToRemovePanel);

                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                splitPane.setLeftComponent(analysePanel);
                splitPane.setRightComponent(duplicateFilesToRemovePanel);
                splitPane.setDividerLocation(0.5);
                splitPane.setResizeWeight(0.7);

                frame.add(stampPanel, BorderLayout.NORTH);
                frame.add(splitPane, BorderLayout.CENTER);
                frame.setSize(1500, 800);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
