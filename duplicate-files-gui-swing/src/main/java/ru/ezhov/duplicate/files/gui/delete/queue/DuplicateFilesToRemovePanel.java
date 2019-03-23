package ru.ezhov.duplicate.files.gui.delete.queue;

import net.coobird.thumbnailator.Thumbnails;
import ru.ezhov.duplicate.files.gui.analyse.MarkToDeleteFileListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class DuplicateFilesToRemovePanel extends JPanel implements MarkToDeleteFileListener {

    private JTextField textFieldPathToSave = new JTextField("D:/");
    private JButton buttonSave = new JButton("Сохранить");


    private JList<DeleteFile> listDuplicateFilesToRemove;
    private DefaultListModel<DeleteFile> listModel;

    public DuplicateFilesToRemovePanel() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Подготовленные файлы для удаления")
        ));
        textFieldPathToSave.setEditable(false);
        buttonSave.addActionListener(e -> {
            File file = new File(textFieldPathToSave.getText() + "deleted-duplicate-files-" + System.currentTimeMillis() + ".xml.dblf");
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
                xmlStreamWriter.writeStartDocument();
                xmlStreamWriter.writeStartElement("duplicate-files");
                xmlStreamWriter.writeAttribute("type", "delete-queue");
                xmlStreamWriter.writeAttribute("version", "0.1");
                for (int i = 0; i < listModel.getSize(); i++) {
                    xmlStreamWriter.writeStartElement("file");
                    xmlStreamWriter.writeCharacters(listModel.get(i).getFile().getAbsolutePath());
                    xmlStreamWriter.writeEndElement();
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndDocument();
                xmlStreamWriter.close();

                JOptionPane.showMessageDialog(this, "Файл для удаления сформирован по пути: " + file.getAbsolutePath(), "Файл для удаления", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        JPanel panelSave = new JPanel(new BorderLayout());
        panelSave.add(textFieldPathToSave, BorderLayout.CENTER);
        panelSave.add(buttonSave, BorderLayout.EAST);

        listModel = new DefaultListModel<>();
        listDuplicateFilesToRemove = new JList<>();
        listDuplicateFilesToRemove.setFixedCellHeight(50);
        listDuplicateFilesToRemove.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                DeleteFile deleteFile = (DeleteFile) value;
                label.setText(deleteFile.getFile().getAbsolutePath());
                BufferedImage imageIcon = deleteFile.getImageIcon();
                if (imageIcon != null) {
                    label.setIcon(new ImageIcon(imageIcon));
                }
                return label;
            }
        });
        listDuplicateFilesToRemove.setModel(listModel);

        add(panelSave, BorderLayout.NORTH);
        add(new JScrollPane(listDuplicateFilesToRemove), BorderLayout.CENTER);
    }

    @Override
    public void mark(String filePath) {
        SwingUtilities.invokeLater(() -> {
            if (!listModel.contains(filePath)) {
                DeleteFile deleteFile = new DeleteFile(filePath);
                listModel.addElement(deleteFile);
            }
        });
    }

    @Override
    public void removeMark(String filePath) {
        SwingUtilities.invokeLater(() -> listModel.removeElement(new DeleteFile(filePath)));
    }

    public boolean isMarkAsDeleted(String pathFile) {
        return listModel.contains(pathFile);
    }

    private class DeleteFile {
        private String name;
        private String path;
        private File file;
        private BufferedImage imageIcon;

        public DeleteFile(String path) {
            this.path = path;
            this.name = name;
            this.file = new File(path);
            try {
                BufferedImage originalImage = ImageIO.read(new File(path));
                BufferedImage thumbnail = Thumbnails.of(originalImage)
                        .size(100, 100)
                        .asBufferedImage();
                imageIcon = thumbnail;
            } catch (Exception e) {
                //пофигу, что не смогли уменьшить
            }
        }

        public String getPath() {
            return path;
        }

        public File getFile() {
            return file;
        }

        public BufferedImage getImageIcon() {
            return imageIcon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeleteFile that = (DeleteFile) o;
            return Objects.equals(path, that.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }
}
