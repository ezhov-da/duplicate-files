package ru.ezhov.duplicate.files.gui.application.delete;

import ru.ezhov.duplicate.files.gui.application.MarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.UnmarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.delete.domain.PreparedToDelete;
import ru.ezhov.duplicate.files.gui.application.repository.ThumbnailsRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.model.domain.FilePath;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DeleteDuplicateFilesQueuePanel extends JPanel implements UnmarkToDeleteListener, MarkToDeleteListener, UploadPreparedDeleteFileListener {

    private java.util.List<UnmarkToDeleteListener> unmarkToDeleteListeners = new ArrayList<>();
    private List<MarkToDeleteListener> markToDeleteListeners = new ArrayList<>();
    private List<UploadPreparedDeleteFileListener> uploadPreparedDeleteFileListeners = new ArrayList<>();


    private JList<PreparedToDelete> listDuplicateFilesToRemove;
    private DefaultListModel<PreparedToDelete> listModel;
    private ThumbnailsRepository thumbnailsRepository;

    private SavePanel savePanel;
    private UploadPanel uploadPanel;

    public DeleteDuplicateFilesQueuePanel(ThumbnailsRepository thumbnailsRepository) {
        this.thumbnailsRepository = thumbnailsRepository;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Подготовленные файлы для удаления")
        ));


        listModel = new DefaultListModel<>();
        listDuplicateFilesToRemove = new JList<>();
        listDuplicateFilesToRemove.setFixedCellHeight(50);
        listDuplicateFilesToRemove.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                PreparedToDelete preparedToDelete = (PreparedToDelete) value;
                label.setText(preparedToDelete.getFile().getAbsolutePath());
                BufferedImage imageIcon = thumbnailsRepository.by(preparedToDelete.getFilePath());
                label.setIcon(new ImageIcon(imageIcon));
                return label;
            }
        });
        listDuplicateFilesToRemove.setModel(listModel);

        savePanel = new SavePanel(listModel);
        uploadPanel = new UploadPanel();

        JPanel panel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);

        uploadPanel.addUploadPreparedDeleteFileListener(this);

        panel.add(savePanel, BorderLayout.NORTH);
        panel.add(uploadPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(listDuplicateFilesToRemove), BorderLayout.CENTER);


    }


    public void addUnmarkToDeleteListener(UnmarkToDeleteListener unmarkToDeleteListener) {
        unmarkToDeleteListeners.add(unmarkToDeleteListener);
    }

    void fireUnmarkToDelete(FilePath filePath) {
        for (UnmarkToDeleteListener unmarkToDeleteListener : unmarkToDeleteListeners) {
            unmarkToDeleteListener.unmark(filePath);
        }
    }

    public void addMarkToDeleteListener(MarkToDeleteListener markToDeleteListener) {
        markToDeleteListeners.add(markToDeleteListener);
    }

    void fireMarkToDelete(FilePath filePath) {
        for (MarkToDeleteListener markToDeleteListener : markToDeleteListeners) {
            markToDeleteListener.mark(filePath);
        }
    }

    public void addUploadPreparedDeleteFileListener(UploadPreparedDeleteFileListener uploadPreparedDeleteFileListener) {
        uploadPreparedDeleteFileListeners.add(uploadPreparedDeleteFileListener);
    }

    void fireUploadPreparedDeleteFileListener(List<FilePath> filePath) {
        for (UploadPreparedDeleteFileListener uploadPreparedDeleteFileListener : uploadPreparedDeleteFileListeners) {
            uploadPreparedDeleteFileListener.upload(filePath);
        }
    }

    @Override
    public void mark(FilePath filePath) {
        SwingUtilities.invokeLater(() -> {
            if (!listModel.contains(new PreparedToDelete(filePath))) {
                listModel.addElement(new PreparedToDelete(filePath));
            }
        });
    }

    @Override
    public void unmark(FilePath filePath) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < listModel.size(); i++) {
                PreparedToDelete preparedToDelete = listModel.get(i);
                if (preparedToDelete.getFilePath().equals(filePath)) {
                    listModel.removeElement(preparedToDelete);
                }
            }
        });
    }

    @Override
    public void upload(List<FilePath> filePaths) {
        SwingUtilities.invokeLater(() -> {
            listModel.removeAllElements();
            for (FilePath filePath : filePaths) {
                listModel.addElement(new PreparedToDelete(filePath));
            }
        });
        fireUploadPreparedDeleteFileListener(filePaths);
    }
}