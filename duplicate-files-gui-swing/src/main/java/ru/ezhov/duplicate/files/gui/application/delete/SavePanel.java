package ru.ezhov.duplicate.files.gui.application.delete;

import ru.ezhov.duplicate.files.gui.application.delete.domain.PreparedToDelete;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class SavePanel extends JPanel {
    private JTextField textFieldPathToSave = new JTextField();
    private JButton buttonSave = new JButton("Сохранить");
    private JButton buttonBrowseFileSave = new JButton("...");
    private DefaultListModel<PreparedToDelete> defaultListModel;

    public SavePanel(DefaultListModel<PreparedToDelete> defaultListModel) {
        this.defaultListModel = defaultListModel;
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Сохранить файл для удаления"));
        buttonBrowseFileSave.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getAbsolutePath().endsWith(".xml.dblfq");
                }

                @Override
                public String getDescription() {
                    return ".xml.dblfq";
                }
            });
            int action = fileChooser.showSaveDialog(SavePanel.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                String fileAbsolutePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileAbsolutePath.endsWith(".xml.dblfq")) {
                    fileAbsolutePath = fileAbsolutePath + ".xml.dblfq";
                }
                String finalFileAbsolutePath = fileAbsolutePath;
                SwingUtilities.invokeLater(() ->
                        textFieldPathToSave.setText(finalFileAbsolutePath));
            }
        });
        buttonSave.addActionListener(e -> {
            File file = new File(textFieldPathToSave.getText());
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
                xmlStreamWriter.writeStartDocument();
                xmlStreamWriter.writeStartElement("duplicate-files");
                xmlStreamWriter.writeAttribute("type", "delete-queue");
                xmlStreamWriter.writeAttribute("version", "0.1");
                xmlStreamWriter.writeStartElement("files");
                for (int i = 0; i < defaultListModel.getSize(); i++) {
                    xmlStreamWriter.writeStartElement("file");
                    xmlStreamWriter.writeCharacters(defaultListModel.get(i).getFile().getAbsolutePath());
                    xmlStreamWriter.writeEndElement();
                }
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndElement();
                xmlStreamWriter.writeEndDocument();
                xmlStreamWriter.close();

                JOptionPane.showMessageDialog(this, "Файл для удаления сформирован по пути: " + file.getAbsolutePath(), "Файл для удаления", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonBrowseFileSave, BorderLayout.CENTER);
        panel.add(buttonSave, BorderLayout.EAST);
        this.add(textFieldPathToSave, BorderLayout.CENTER);
        this.add(panel, BorderLayout.EAST);
    }
}
