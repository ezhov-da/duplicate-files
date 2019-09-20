package ru.ezhov.duplicate.files.gui.application.delete;

import ru.ezhov.duplicate.files.gui.application.MarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.UnmarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.delete.domain.PreparedToDelete;
import ru.ezhov.duplicate.files.gui.application.repository.ThumbnailsRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteDuplicateFilesQueuePanel extends JPanel implements UnmarkToDeleteListener, MarkToDeleteListener, UploadPreparedDeleteFileListener {
    private static final Logger LOG = Logger.getLogger(DeleteDuplicateFilesQueuePanel.class.getName());
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

        JButton buttonClear = new JButton("Очистить список");
        buttonClear.addActionListener(e -> {
            clearModel();
        });

        panel.add(savePanel);
        panel.add(uploadPanel);
        panel.add(buttonClear);
        add(panel, BorderLayout.NORTH);

        add(new JScrollPane(listDuplicateFilesToRemove), BorderLayout.CENTER);

        JPanel panelBottom = new JPanel();
        BoxLayout boxLayoutBottom = new BoxLayout(panelBottom, BoxLayout.Y_AXIS);
        panelBottom.setLayout(boxLayoutBottom);

        JButton buttonDelete = new JButton("Удалить подготовленные файлы");

        buttonDelete.addActionListener(e -> {
            deleteAll();
        });

        panelBottom.add(buttonDelete);
        add(panelBottom, BorderLayout.SOUTH);
    }

    private void deleteAll() {
        AtomicBoolean atomicBooleanCancel = new AtomicBoolean(false);

        DeleteProccesDialog deleteProccesDialog = new DeleteProccesDialog();
        Runnable runnable = () -> {
            List<PreparedToDelete> preparedToDeletes = new ArrayList<>();
            for (int i = 0; i < listModel.size(); i++) {
                PreparedToDelete preparedToDelete = listModel.get(i);
                preparedToDeletes.add(preparedToDelete);
            }

            SwingWorker<String, DeleteProcces> swingWorker = new SwingWorker<String, DeleteProcces>() {
                @Override
                protected String doInBackground() throws Exception {
                    for (int i = 0; i < preparedToDeletes.size(); i++) {
                        if (atomicBooleanCancel.get()) {
                            break;
                        }
                        PreparedToDelete preparedToDelete = preparedToDeletes.get(i);
                        File file = preparedToDelete.getFile();
                        if (file.exists()) {
                            Thread.sleep(250);
                            boolean delete = file.delete();
                            LOG.log(Level.CONFIG, "method=deleteAll stampedOn=\"файл ''{0}'' удален ''{1}''\"", new Object[]{file, delete});
                            process(Collections.singletonList(new DeleteProcces(preparedToDeletes.size(), i + 1, preparedToDelete)));
                        }
                    }
                    return null;
                }

                @Override
                protected void process(List<DeleteProcces> chunks) {
                    chunks.forEach(dp -> {
                        String text = String.format("%s из %s: %s", dp.number, dp.allCount, dp.preparedToDelete.getFilePath().path());
                        deleteProccesDialog.publishInfo(text);
                    });
                }

                @Override
                protected void done() {
                    listDuplicateFilesToRemove.repaint();
                    deleteProccesDialog.setVisible(false);
                    deleteProccesDialog.dispose();
                }
            };

            swingWorker.execute();
        };

        deleteProccesDialog.cancel(() -> {
            atomicBooleanCancel.set(true);
        });
        deleteProccesDialog.showWith(runnable);
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

    private void clearModel() {
        List<PreparedToDelete> preparedToDeletes = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            PreparedToDelete preparedToDelete = listModel.get(i);
            preparedToDeletes.add(preparedToDelete);
        }

        SwingUtilities.invokeLater(() -> {
            preparedToDeletes.forEach(pd -> {
                listModel.removeElement(pd);
                fireUnmarkToDelete(pd.getFilePath());
            });
        });
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

    private class DeleteProcces {
        private int allCount;
        private int number;
        private PreparedToDelete preparedToDelete;

        public DeleteProcces(int allCount, int number, PreparedToDelete preparedToDelete) {
            this.allCount = allCount;
            this.number = number;
            this.preparedToDelete = preparedToDelete;
        }

        public int getAllCount() {
            return allCount;
        }

        public int getNumber() {
            return number;
        }

        public PreparedToDelete getPreparedToDelete() {
            return preparedToDelete;
        }
    }
}