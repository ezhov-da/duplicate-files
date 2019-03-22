package ru.ezhov.duplicate.files.gui.analyse;

import javax.swing.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class DuplicateFilesToRemovePanel extends JPanel implements MarkToDeleteFileListener {

    private JTextField textFieldPathToSave = new JTextField("D:/");
    private JButton buttonSave = new JButton("Сохранить");


    private JList<String> listDuplicateFilesToRemove;
    private DefaultListModel<String> listModel;

    public DuplicateFilesToRemovePanel() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        textFieldPathToSave.setEditable(false);
        buttonSave.addActionListener(e -> {
            File file = new File(textFieldPathToSave.getText() + "deleted-duplicate-files-" + System.currentTimeMillis() + ".xml.dblf");
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
                xmlStreamWriter.writeStartDocument();
                xmlStreamWriter.writeStartElement("deleted-files");
                for (int i = 0; i < listModel.getSize(); i++) {
                    xmlStreamWriter.writeStartElement("file");
                    xmlStreamWriter.writeCharacters(listModel.get(i));
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
        listDuplicateFilesToRemove.setModel(listModel);

        add(panelSave, BorderLayout.NORTH);
        add(new JScrollPane(listDuplicateFilesToRemove), BorderLayout.CENTER);
    }

    @Override
    public void mark(String filePath) {
        SwingUtilities.invokeLater(() -> {
            if (!listModel.contains(filePath)) {
                listModel.addElement(filePath);
            }
        });
    }

    @Override
    public void removeMark(String filePath) {
        SwingUtilities.invokeLater(() -> listModel.removeElement(filePath));
    }

    public boolean isMarkAsDeleted(String pathFile) {
        return listModel.contains(pathFile);
    }
}
