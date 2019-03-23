package ru.ezhov.duplicate.files.gui.stamp;

import ru.ezhov.duplicate.files.core.stamp.generator.service.XmlFileBruteForceCreator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StampPanel extends JPanel {

    private JTextField textFieldRootPathFileStampGenerator;
    private JTextField textFieldReportStampGenerator;
    private JButton buttonBrowseFileRootPathFile;
    private JButton buttonBrowseReportStampGenerator;
    private JButton buttonStartStampGenerator;
    private JButton buttonStopStampGenerator;
    private JLabel labelStampGeneratorInfo;

    public StampPanel() {
        setLayout(new BorderLayout());
        init();
        addListeners();
    }

    private void init() {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Создание отпечатков для файлов")
        ));

        textFieldRootPathFileStampGenerator = new JTextField();
        buttonBrowseReportStampGenerator = new JButton("...");
        JPanel panelBrowseRootPathFile = new JPanel(new BorderLayout());
        panelBrowseRootPathFile.add(textFieldRootPathFileStampGenerator, BorderLayout.CENTER);
        panelBrowseRootPathFile.add(buttonBrowseReportStampGenerator, BorderLayout.EAST);

        textFieldReportStampGenerator = new JTextField();
        buttonBrowseFileRootPathFile = new JButton("...");
        JPanel panelBrowseReportFile = new JPanel(new BorderLayout());
        panelBrowseReportFile.add(textFieldReportStampGenerator, BorderLayout.CENTER);
        panelBrowseReportFile.add(buttonBrowseFileRootPathFile, BorderLayout.EAST);


        buttonStartStampGenerator = new JButton("Запустить создание отпечатков файлов");
        buttonStopStampGenerator = new JButton("Остановить создание отпечатков файлов");
        buttonStopStampGenerator.setEnabled(false);
        JPanel panelStamps = new JPanel(new BorderLayout());
        panelStamps.add(buttonStartStampGenerator, BorderLayout.CENTER);
        panelStamps.add(buttonStopStampGenerator, BorderLayout.SOUTH);

        JPanel panelBrowse = new JPanel(new BorderLayout());
        panelBrowse.add(panelBrowseRootPathFile, BorderLayout.CENTER);
        panelBrowse.add(panelBrowseReportFile, BorderLayout.SOUTH);

        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelBrowse, BorderLayout.CENTER);
        panelTop.add(panelStamps, BorderLayout.EAST);

        labelStampGeneratorInfo = new JLabel("Укажите корневую директорию для создания отпечатков файлов и путь для файла отпечатков");
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(labelStampGeneratorInfo, BorderLayout.CENTER);

        add(panelTop, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
    }

    private StampWorker currentStampWorker;

    private void addListeners() {
        buttonBrowseReportStampGenerator.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int action = fileChooser.showOpenDialog(StampPanel.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() ->
                        textFieldRootPathFileStampGenerator.setText(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        });

        buttonStartStampGenerator.addActionListener(e -> {
            currentStampWorker = new StampWorker(
                    new File(StampPanel.this.textFieldRootPathFileStampGenerator.getText()),
                    new File(StampPanel.this.textFieldReportStampGenerator.getText())
            );
            buttonStartStampGenerator.setEnabled(false);
            buttonStopStampGenerator.setEnabled(true);
            currentStampWorker.execute();
        });

        buttonStopStampGenerator.addActionListener(e -> {
            if (currentStampWorker != null) {
                currentStampWorker.cancel(true);
                buttonStartStampGenerator.setEnabled(true);
                buttonStopStampGenerator.setEnabled(false);
            }
        });
    }

    private class StampWorker extends SwingWorker<String, String> {

        private File root;
        private File report;
        private XmlFileBruteForceCreator xmlFileBruteForceCreator;

        public StampWorker(File root, File report) {
            this.root = root;
            this.report = report;
            xmlFileBruteForceCreator = new XmlFileBruteForceCreator(root, report);
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
            StampWorker.this.publish("Отпечатки файлов сохранены по пути: " + textFieldReportStampGenerator.getText());
            return null;
        }
    }
}
