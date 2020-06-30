package ru.ezhov.duplicate.files.gui;

import ru.ezhov.duplicate.files.gui.application.analyse.AnalysePanel;
import ru.ezhov.duplicate.files.gui.application.delete.DeleteDuplicateFilesQueuePanel;
import ru.ezhov.duplicate.files.gui.application.repository.ThumbnailsRepository;
import ru.ezhov.duplicate.files.gui.application.stamp.StampPanel;
import ru.ezhov.duplicate.files.gui.infrastructure.repository.TempDirectoryCacheThumbnailRepository;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;

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
                JFrame frame = new JFrame("Duplicates");
                frame.setIconImage(new ImageIcon(DuplicateFilesGuiApplication.class.getResource("/duplicate_16x16.png")).getImage());

                ThumbnailsRepository thumbnailsRepository = new TempDirectoryCacheThumbnailRepository();

                StampPanel stampPanel = new StampPanel();
                AnalysePanel analysePanel = new AnalysePanel(thumbnailsRepository);
                DeleteDuplicateFilesQueuePanel deleteDuplicateFilesQueuePanel = new DeleteDuplicateFilesQueuePanel(thumbnailsRepository);

                analysePanel.addMarkToDeleteListener(deleteDuplicateFilesQueuePanel);
                analysePanel.addUnmarkToDeleteListener(deleteDuplicateFilesQueuePanel);

                deleteDuplicateFilesQueuePanel.addMarkToDeleteListener(analysePanel);
                deleteDuplicateFilesQueuePanel.addUnmarkToDeleteListener(analysePanel);
                deleteDuplicateFilesQueuePanel.addUploadPreparedDeleteFileListener(analysePanel);

                JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                splitPane.setLeftComponent(analysePanel);
                splitPane.setRightComponent(deleteDuplicateFilesQueuePanel);
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
