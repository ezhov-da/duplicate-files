package ru.ezhov.duplicate.files.gui.application.delete;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.ezhov.duplicate.files.gui.application.delete.domain.PreparedToDelete;
import ru.ezhov.duplicate.files.stamp.analyzer.domain.FilePath;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadPanel extends JPanel {

    private List<UploadPreparedDeleteFileListener> preparedDeleteFileListeners = new ArrayList<>();
    private JTextField textFieldPathToSave = new JTextField();
    private JButton buttonOpen = new JButton("Open");
    private JButton buttonBrowseFileUpload = new JButton("...");
    private DefaultListModel<PreparedToDelete> defaultListModel;

    public UploadPanel() {
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder("Download file to delete"));
        buttonBrowseFileUpload.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            fileChooser.setFileFilter(new FileFilter() {
//                @Override
//                public boolean accept(File f) {
//                    return f.isDirectory() || f.getAbsolutePath().endsWith(".xml.dblfq");
//                }
//
//                @Override
//                public String getDescription() {
//                    return ".xml.dblfq";
//                }
//            });
            int action = fileChooser.showSaveDialog(UploadPanel.this);
//            if (stampedOn == JFileChooser.APPROVE_OPTION) {
//                String fileAbsolutePath = fileChooser.getSelectedFile().getAbsolutePath();
//                if (!fileAbsolutePath.endsWith(".xml.dblfq")) {
//                    fileAbsolutePath = fileAbsolutePath + ".xml.dblfq";
//                }
//                String finalFileAbsolutePath = fileAbsolutePath;
//                SwingUtilities.invokeLater(() ->
//                        textFieldPathToSave.setText(finalFileAbsolutePath));
//            }
        });
        buttonOpen.addActionListener(e -> {
            File file = new File(textFieldPathToSave.getText());

            XPathFactory xPathFactory = XPathFactory.newInstance();

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                NodeList nodeList = (NodeList) xPathFactory.newXPath().evaluate("//duplicate-files/files/file", new InputSource(fileInputStream), XPathConstants.NODESET);
                List<FilePath> filePaths = new ArrayList<>();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node item = nodeList.item(i);
                    filePaths.add(new FilePath() {

                        @Override
                        public String path() {
                            return item.getTextContent();
                        }
                    });
                }
                fireUploadPreparedDeleteFileListener(filePaths);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening file", "Could not open file", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonBrowseFileUpload, BorderLayout.CENTER);
        panel.add(buttonOpen, BorderLayout.EAST);
        this.add(textFieldPathToSave, BorderLayout.CENTER);
        this.add(panel, BorderLayout.EAST);
    }

    public void addUploadPreparedDeleteFileListener(UploadPreparedDeleteFileListener uploadPreparedDeleteFileListener) {
        preparedDeleteFileListeners.add(uploadPreparedDeleteFileListener);
    }

    private void fireUploadPreparedDeleteFileListener(List<FilePath> filePaths) {
        for (UploadPreparedDeleteFileListener uploadPreparedDeleteFileListener : preparedDeleteFileListeners) {
            uploadPreparedDeleteFileListener.upload(filePaths);
        }
    }
}
