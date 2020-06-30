package ru.ezhov.duplicate.files.gui.application.stamp;

import ru.ezhov.duplicate.files.stamp.generator.infrastructure.repository.FingerprintFileRepositoryFactory;
import ru.ezhov.duplicate.files.stamp.generator.infrastructure.service.FingerprintFileServiceFactory;
import ru.ezhov.duplicate.files.stamp.generator.model.domain.FileStamp;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepository;
import ru.ezhov.duplicate.files.stamp.generator.model.repository.FingerprintFileRepositoryException;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileService;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileServiceAlreadyStoppedException;
import ru.ezhov.duplicate.files.stamp.generator.model.service.FingerprintFileServiceException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
                BorderFactory.createTitledBorder("File stamp creation")
        ));

        textFieldRootPathFileStampGenerator = new JTextField();
        buttonBrowseFileRootPathFile = new JButton("...");
        JPanel panelBrowseRootPathFile = new JPanel(new BorderLayout());
        panelBrowseRootPathFile.add(textFieldRootPathFileStampGenerator, BorderLayout.CENTER);
        panelBrowseRootPathFile.add(buttonBrowseFileRootPathFile, BorderLayout.EAST);

        textFieldReportStampGenerator = new JTextField();
        buttonBrowseReportStampGenerator = new JButton("...");
        JPanel panelBrowseReportFile = new JPanel(new BorderLayout());
        panelBrowseReportFile.add(textFieldReportStampGenerator, BorderLayout.CENTER);
        panelBrowseReportFile.add(buttonBrowseReportStampGenerator, BorderLayout.EAST);


        buttonStartStampGenerator = new JButton("Start file stamp creation");
        buttonStopStampGenerator = new JButton("Stop file stamp creation");
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

        labelStampGeneratorInfo = new JLabel("Specify the root directory for creating fingerprints of the files and the path for the fingerprint file");
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.add(labelStampGeneratorInfo, BorderLayout.CENTER);

        add(panelTop, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
    }

    private StampWorker currentStampWorker;

    private void addListeners() {
        buttonBrowseFileRootPathFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int action = fileChooser.showOpenDialog(StampPanel.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                SwingUtilities.invokeLater(() ->
                        textFieldRootPathFileStampGenerator.setText(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        });

        buttonBrowseReportStampGenerator.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getAbsolutePath().endsWith(".xml.dblfs");
                }

                @Override
                public String getDescription() {
                    return ".xml.dblfs";
                }
            });
            int action = fileChooser.showSaveDialog(StampPanel.this);
            if (action == JFileChooser.APPROVE_OPTION) {
                String fileAbsolutePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileAbsolutePath.endsWith(".xml.dblfs")) {
                    fileAbsolutePath = fileAbsolutePath + ".xml.dblfs";
                }
                String finalFileAbsolutePath = fileAbsolutePath;
                SwingUtilities.invokeLater(() ->
                        textFieldReportStampGenerator.setText(finalFileAbsolutePath));
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

    private class StampWorker extends SwingWorker<String, FileStamp> {
        private File root;
        private File report;
        private FingerprintFileService fingerprintFileService;
        private FingerprintFileRepository fingerprintFileRepository;
        private AtomicInteger counterFiles = new AtomicInteger();
        private List<FileStamp> fileStamps = new ArrayList<>();

        public StampWorker(File root, File report) {
            this.root = root;
            this.report = report;
            fingerprintFileService = FingerprintFileServiceFactory.newInstance(root);
            fingerprintFileRepository = FingerprintFileRepositoryFactory.newInstance(report);
        }

        @Override
        protected void process(List<FileStamp> chunks) {
            FileStamp fileStamp = chunks.get(0);
            String text =
                    "<html>Processed files : <b>" + counterFiles.get() +
                            " </b>Current: <i>" + chunks.get(0) + ". Size: " + BigDecimal.valueOf((fileStamp.file().length() / 1024D / 1024D)).setScale(2, RoundingMode.UP).doubleValue() + " MB</i>";
            StampPanel.this.labelStampGeneratorInfo.setText(text);
        }

        @Override
        protected String doInBackground() throws FingerprintFileServiceException {
            fingerprintFileService.start(
                    fileStamp -> {
                        if (StampWorker.this.isCancelled()) {
                            try {
                                fingerprintFileService.stop();
                            } catch (FingerprintFileServiceAlreadyStoppedException e) {
                                e.printStackTrace();
                            }
                        }
                        fileStamps.add(fileStamp);
                        counterFiles.incrementAndGet();
                        StampWorker.this.publish(fileStamp);
                    }
            );
            return null;
        }

        @Override
        protected void done() {
            String text;
            try {
                fingerprintFileRepository.save(fileStamps);
                text = "<html>Files processed: <b>" + counterFiles.get() +
                        " </b>Fingerprints saved along the path: " + textFieldReportStampGenerator.getText();
                StampPanel.this.labelStampGeneratorInfo.setText(text);
            } catch (FingerprintFileRepositoryException e) {
                text = "<html><font color=\"red\">Files processed: <b>" + counterFiles.get() +
                        " </b>Error saving fingerprints to file " + textFieldReportStampGenerator.getText() + "</font>";
                e.printStackTrace();
            }
            StampPanel.this.labelStampGeneratorInfo.setText(text);

            buttonStartStampGenerator.setEnabled(true);
            buttonStopStampGenerator.setEnabled(false);
        }
    }
}
