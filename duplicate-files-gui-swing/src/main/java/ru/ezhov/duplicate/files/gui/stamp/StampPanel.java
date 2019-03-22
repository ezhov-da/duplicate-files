package ru.ezhov.duplicate.files.gui.stamp;

import ru.ezhov.duplicate.files.core.stamp.generator.service.FileListener;
import ru.ezhov.duplicate.files.core.stamp.generator.service.XmlFileBruteForceCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class StampPanel extends JPanel {

    private JTextField rootPathForStampGenerator;
    private JButton buttonBrowseFile;
    private JButton buttonStartStampGenerator;
    private JButton buttonStopStampGenerator;
    private JLabel labelStampGeneratorInfo;

    public StampPanel() {
        setLayout(new BorderLayout());
        init();
        addListeners();
    }

    private void init() {
        rootPathForStampGenerator = new JTextField();
        buttonBrowseFile = new JButton("...");
        buttonStartStampGenerator = new JButton("Запустить отпечаток файлов");
        buttonStopStampGenerator = new JButton("Остановить отпечаток файлов");
        labelStampGeneratorInfo = new JLabel();

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(rootPathForStampGenerator, BorderLayout.CENTER);
        JPanel panelTopEast = new JPanel();
        panelTopEast.setLayout(new BoxLayout(panelTopEast, BoxLayout.LINE_AXIS));
        panelTopEast.add(buttonBrowseFile);
        panelTopEast.add(buttonStartStampGenerator);
        panelTopEast.add(buttonStopStampGenerator);
        panelTop.add(panelTopEast, BorderLayout.EAST);

        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(labelStampGeneratorInfo, BorderLayout.CENTER);

        add(panelTop, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
    }

    private StampWorker currentStampWorker;

    private void addListeners() {
        buttonBrowseFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int action = fileChooser.showOpenDialog(StampPanel.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() ->
                        rootPathForStampGenerator.setText(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        });

        buttonStartStampGenerator.addActionListener(e -> {
            currentStampWorker = new StampWorker(new File(StampPanel.this.rootPathForStampGenerator.getText()));
            currentStampWorker.execute();
        });

        buttonStopStampGenerator.addActionListener(e -> {
            if (currentStampWorker != null) {
                currentStampWorker.cancel(true);
            }
        });
    }

    private class StampWorker extends SwingWorker<String, String> {

        private File root;
        private XmlFileBruteForceCreator xmlFileBruteForceCreator;

        public StampWorker(File root) {
            this.root = root;
            xmlFileBruteForceCreator = new XmlFileBruteForceCreator(root, new File("D:/1.xml"));
        }

        @Override
        protected void process(List<String> chunks) {
            StampPanel.this.labelStampGeneratorInfo.setText(chunks.get(0));
        }

        @Override
        protected String doInBackground() throws Exception {
            xmlFileBruteForceCreator.run(absoluteFilePath -> {
                if (StampWorker.this.isCancelled()) {
                    xmlFileBruteForceCreator.stop();
                }
                StampWorker.this.publish(absoluteFilePath);
            });
            StampWorker.this.publish("Отпечатки файлов сохранены по пути: " + rootPathForStampGenerator.getText());
            return null;
        }
    }
}
