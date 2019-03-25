package ru.ezhov.duplicate.files.gui.analyse;

import net.coobird.thumbnailator.Thumbnails;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import ru.ezhov.duplicate.files.core.stamp.analyzer.service.DuplicateFilesAnalyserService;
import ru.ezhov.duplicate.files.core.stamp.analyzer.service.DuplicateFilesAnalyserServiceException;
import ru.ezhov.duplicate.files.gui.analyse.domain.Md5Hash;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class AnalysePanel extends JPanel {
    private JPanel panelMock;
    private LoadStampFilePanel loadStampFilePanel;
    private AnalyseResultTreeTablePanel analyseResultTreeTablePanel;
    private DeleteDuplicateFilesQueuePanel deleteDuplicateFilesQueuePanel;
    private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private JPanel analysePanel;
    private Map<String, DuplicateFile> pathsToDeleted = new HashMap<>();

    private List<MarkToDeleteFileListener> markToDeleteFileListeners = new ArrayList<>();

    public AnalysePanel() throws Exception {
        init();
    }

    private void addPathToDelete(DuplicateFile duplicateFile) {
        if (!pathsToDeleted.containsKey(duplicateFile.getPath())) {
            pathsToDeleted.put(duplicateFile.getPath(), duplicateFile);
            for (MarkToDeleteFileListener markToDeleteFileListener : markToDeleteFileListeners) {
                markToDeleteFileListener.addToDeleted(duplicateFile);
            }
        }
    }

    private void removePathToDelete(DuplicateFile duplicateFile) {
        if (pathsToDeleted.containsKey(duplicateFile.getPath())) {
            pathsToDeleted.remove(duplicateFile.getPath());
            for (MarkToDeleteFileListener markToDeleteFileListener : markToDeleteFileListeners) {
                markToDeleteFileListener.removeToDeleted(duplicateFile);
            }
        }
    }

    private boolean isDeleted(String path) {
        return pathsToDeleted.containsKey(path);
    }

    private void init() throws Exception {
        this.loadStampFilePanel = new LoadStampFilePanel();
        this.deleteDuplicateFilesQueuePanel = new DeleteDuplicateFilesQueuePanel();

        markToDeleteFileListeners.add(deleteDuplicateFilesQueuePanel);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder("Анализ отпечатков и выбор файлов для удаления")
        ));

        panelMock = new JPanel(new BorderLayout());
        JLabel labelMock = new JLabel("Выберите файл отпечатков и запустите анализ на дубликаты");
        labelMock.setHorizontalTextPosition(SwingConstants.CENTER);
        panelMock.add(labelMock, BorderLayout.CENTER);

        analysePanel = new JPanel(new BorderLayout());
        analysePanel.add(loadStampFilePanel, BorderLayout.NORTH);
        analysePanel.add(panelMock, BorderLayout.CENTER);


        splitPane.setLeftComponent(analysePanel);
        splitPane.setRightComponent(deleteDuplicateFilesQueuePanel);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.7);

        add(splitPane, BorderLayout.CENTER);
    }

    private class LoadStampFilePanel extends JPanel {
        private JTextField textFieldFileStampGenerator;
        private JButton buttonBrowseFileStampGenerator;
        private JButton buttonAnalyseStampGenerator;

        public LoadStampFilePanel() {
            setLayout(new BorderLayout());
            textFieldFileStampGenerator = new JTextField();
            buttonBrowseFileStampGenerator = new JButton("...");
            buttonAnalyseStampGenerator = new JButton("Анализировать");
            buttonBrowseFileStampGenerator.addActionListener(e -> {
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
                int action = fileChooser.showOpenDialog(AnalysePanel.this);
                if (action == JFileChooser.APPROVE_OPTION) {
                    SwingUtilities.invokeLater(() ->
                            textFieldFileStampGenerator.setText(fileChooser.getSelectedFile().getAbsolutePath()));
                }
            });
            buttonAnalyseStampGenerator.addActionListener(e -> {
                DuplicateFilesAnalyserService duplicateFilesAnalyserService = new DuplicateFilesAnalyserService();
                try {
                    Map<String, List<String>> map = duplicateFilesAnalyserService.findDuplicate(new File(textFieldFileStampGenerator.getText()));
                    AnalyseResultTreeTablePanel newPanelAnalyseResult = new AnalyseResultTreeTablePanel(map);
                    if (panelMock != null) {
                        SwingUtilities.invokeLater(() -> {
                            analysePanel.remove(panelMock);
                            panelMock = null;
                        });
                    }
                    if (analyseResultTreeTablePanel != null) {
                        SwingUtilities.invokeLater(() -> {
                            analysePanel.remove(analyseResultTreeTablePanel);
                            analyseResultTreeTablePanel = newPanelAnalyseResult;
                            analysePanel.add(analyseResultTreeTablePanel, BorderLayout.CENTER);
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            analyseResultTreeTablePanel = newPanelAnalyseResult;
                            analysePanel.add(analyseResultTreeTablePanel, BorderLayout.CENTER);
                        });
                    }
                    SwingUtilities.invokeLater(() -> {
                        analysePanel.revalidate();
                        analysePanel.repaint();
                    });
                } catch (DuplicateFilesAnalyserServiceException e1) {
                    e1.printStackTrace();
                }
            });
            JPanel panelBrowse = new JPanel(new BorderLayout());
            panelBrowse.add(textFieldFileStampGenerator, BorderLayout.CENTER);
            JPanel panelBrowseButtons = new JPanel(new BorderLayout());
            panelBrowseButtons.add(buttonBrowseFileStampGenerator, BorderLayout.WEST);
            panelBrowseButtons.add(buttonAnalyseStampGenerator, BorderLayout.CENTER);
            panelBrowse.add(panelBrowseButtons, BorderLayout.EAST);

            add(panelBrowse, BorderLayout.CENTER);
        }
    }

    private class AnalyseResultTreeTablePanel extends JPanel {

        private List<MarkToDeleteFileListener> markToDeleteFileListeners = new ArrayList<>();
        private JPanel panelPaginator;
        private java.util.List<Map.Entry<String, java.util.List<String>>> entries;
        private JXTreeTable treeTable;
        private TreeTableModel treeTableModel;

        public AnalyseResultTreeTablePanel(Map<String, List<String>> map) {
            setLayout(new BorderLayout());
            entries = new ArrayList<>();
            for (Map.Entry<String, java.util.List<String>> entry : map.entrySet()) {
                if (entry.getValue().size() > 1) {
                    entries.add(entry);
                }
            }
            java.util.List<Map.Entry<String, java.util.List<String>>> part = entries.subList(0, entries.size() > 10 ? 10 : entries.size());
            treeTableModel = createFrom(part);
            treeTable = new JXTreeTable(treeTableModel);

            int pages = (int) Math.ceil(entries.size() / 10D);

            panelPaginator = new JPanel();
            JSpinner spinnerPage = new JSpinner(new SpinnerNumberModel(1, 1, pages, 1));
            JLabel labelAllPages = new JLabel(pages + "");
            panelPaginator.add(spinnerPage);
            panelPaginator.add(labelAllPages);

            spinnerPage.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    changePage(spinnerPage);
                }
            });

            ((JSpinner.DefaultEditor) spinnerPage.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                        return;
                    }
                    changePage(spinnerPage);

                }
            });
            treeTable.setTreeCellRenderer(new DefaultXTreeCellRenderer() {
                @Override
                public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                              boolean leaf, int row, boolean hasFocus) {
                    JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                    DefaultMutableTreeTableNode dftn = (DefaultMutableTreeTableNode) value;
                    if (dftn.getUserObject() instanceof DuplicateFile) {
                        DuplicateFile duplicateFile = (DuplicateFile) dftn.getUserObject();
                        if (duplicateFile.isMarkDeleted()) {
                            label.setText("<html><s>" + duplicateFile.getPath() + "</s>");
                            label.setForeground(Color.GRAY);
                        }
                    }
                    return label;
                }


            });
            treeTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    int column = treeTable.columnAtPoint(e.getPoint());
                    int row = treeTable.getSelectedRow();
                    int columnModel = treeTable.convertColumnIndexToModel(column);
                    if (columnModel == 2) {
                        Object value = treeTable.getValueAt(row, 0);
                        if (value instanceof DuplicateFile) {
                            DuplicateFile duplicateFile = (DuplicateFile) value;
                            try {
                                Desktop.getDesktop().open(new File(duplicateFile.getPath()));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else if (columnModel == 3) {
                        Object value = treeTable.getValueAt(row, 0);
                        if (value instanceof DuplicateFile) {
                            DuplicateFile duplicateFile = (DuplicateFile) value;
                            if (duplicateFile.isMarkDeleted()) {
                                SwingUtilities.invokeLater(() -> {
                                    treeTable.repaint();
                                });
                                removePathToDelete(duplicateFile);
                            } else {
                                SwingUtilities.invokeLater(() -> {
                                    treeTable.repaint();
                                });
                                addPathToDelete(duplicateFile);
                            }
                        }
                    }
                }
            });

            initTreeTable(treeTable);
            add(new JScrollPane(treeTable), BorderLayout.CENTER);
            add(panelPaginator, BorderLayout.SOUTH);
        }

        private void changePage(JSpinner spinner) {
            int pageNumber = Integer.valueOf(spinner.getModel().getValue() + "");
            java.util.List<Map.Entry<String, java.util.List<String>>> part;
            if (pageNumber - 1 == 0) {
                part = entries.subList(0, entries.size() > 10 ? 10 : entries.size());
            } else {
                int pnStart = (pageNumber - 1) * 10;
                int pnEnd = pageNumber * 10;
                part = entries.subList(pnStart, entries.size() > pnEnd ? pnEnd : entries.size());
            }
            treeTableModel = createFrom(part);
            treeTable.setTreeTableModel(treeTableModel);
            SwingUtilities.invokeLater(() -> {
                initTreeTable(treeTable);
            });
        }

        private void initTreeTable(JXTreeTable treeTable) {
            treeTable.setRowHeight(50);
            treeTable.getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (value instanceof DuplicateFile) {
                        DuplicateFile duplicateFile = (DuplicateFile) value;
                        if (duplicateFile.getImageIcon() != null) {
                            label.setIcon(new ImageIcon(duplicateFile.getImageIcon()));
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                        }
                    } else {
                        label.setIcon(null);
                    }
                    label.setText("");
                    return label;
                }
            });
            treeTable.getColumn(1).setWidth(100);
            treeTable.getColumn(1).setMaxWidth(100);

            treeTable.getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (value instanceof DuplicateFile) {
                        label.setIcon(new ImageIcon(getClass().getResource("/preview_16x16.png")));
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        label.setIcon(null);
                    }
                    label.setText("");
                    return label;
                }
            });
            treeTable.getColumn(2).setWidth(25);
            treeTable.getColumn(2).setMaxWidth(25);
            treeTable.getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (value instanceof DuplicateFile) {
                        DuplicateFile duplicateFile = (DuplicateFile) value;
                        if (duplicateFile.isMarkDeleted()) {
                            label.setIcon(new ImageIcon(getClass().getResource("/list-delete_16x16.png")));
                        } else {
                            label.setIcon(new ImageIcon(getClass().getResource("/list-add_16x16.png")));
                        }
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                    } else {
                        label.setIcon(null);
                    }
                    label.setText("");
                    return label;
                }
            });
            treeTable.getColumn(3).setWidth(25);
            treeTable.getColumn(3).setMaxWidth(25);
            treeTable.expandAll();
        }

        private TreeTableModel createFrom(java.util.List<Map.Entry<String, java.util.List<String>>> entries) {
            DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
            for (Map.Entry<String, List<String>> entry : entries) {
                DefaultMutableTreeTableNode hashNode = new DefaultMutableTreeTableNode(new Md5Hash(entry.getKey()));
                for (String filePath : entry.getValue()) {
                    DuplicateFile duplicateFile = new DuplicateFile(filePath);
                    hashNode.add(new DefaultMutableTreeTableNode(duplicateFile));
                }
                root.add(hashNode);
            }
            return new DefaultTreeTableModel(root, Arrays.asList("Файл", "Превью", "Открыть", "Добавить в удаляемые")) {
                @Override
                public Object getValueAt(Object node, int column) {
                    DefaultMutableTreeTableNode mtn = (DefaultMutableTreeTableNode) node;
                    if (mtn.getUserObject() instanceof DuplicateFile) {
                        DuplicateFile duplicateFile = (DuplicateFile) mtn.getUserObject();
                        switch (column) {

                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                return duplicateFile;
                            default:
                                return super.getValueAt(node, column);
                        }

                    } else {
                        return super.getValueAt(node, column);

                    }
                }
            };
        }
    }

    private class DeleteDuplicateFilesQueuePanel extends JPanel implements MarkToDeleteFileListener {

        private JTextField textFieldPathToSave = new JTextField();
        private JButton buttonSave = new JButton("Сохранить");
        private JButton buttonBrowseFileSave = new JButton("...");
        private JList<DuplicateFile> listDuplicateFilesToRemove;
        private DefaultListModel<DuplicateFile> listModel;

        public DeleteDuplicateFilesQueuePanel() {
            init();
        }

        private void init() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5),
                    BorderFactory.createTitledBorder("Подготовленные файлы для удаления")
            ));
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
                int action = fileChooser.showSaveDialog(AnalysePanel.this);
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
                    for (int i = 0; i < listModel.getSize(); i++) {
                        xmlStreamWriter.writeStartElement("file");
                        xmlStreamWriter.writeCharacters(listModel.get(i).getFile().getAbsolutePath());
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
            JPanel panelSave = new JPanel(new BorderLayout());
            panelSave.add(textFieldPathToSave, BorderLayout.CENTER);
            panelSave.add(panel, BorderLayout.EAST);


            listModel = new DefaultListModel<>();
            listDuplicateFilesToRemove = new JList<>();
            listDuplicateFilesToRemove.setFixedCellHeight(50);
            listDuplicateFilesToRemove.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    DuplicateFile duplicateFile = (DuplicateFile) value;
                    label.setText(duplicateFile.getFile().getAbsolutePath());
                    BufferedImage imageIcon = duplicateFile.getImageIcon();
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
        public void addToDeleted(DuplicateFile duplicateFile) {
            SwingUtilities.invokeLater(() -> {
                if (!listModel.contains(duplicateFile)) {
                    listModel.addElement(duplicateFile);
                }
            });

        }

        @Override
        public void removeToDeleted(DuplicateFile duplicateFile) {
            SwingUtilities.invokeLater(() -> {
                if (listModel.contains(duplicateFile)) {
                    listModel.removeElement(duplicateFile);
                }
            });
        }
    }

    private class DuplicateFile {
        private String path;
        private File file;
        private boolean markDeleted;

        private BufferedImage imageIcon;

        public DuplicateFile(String path) {
            this.path = path;
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

        public BufferedImage getImageIcon() {
            return imageIcon;
        }

        public String getPath() {
            return path;
        }

        public boolean isMarkDeleted() {
            return isDeleted(path);
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return path;
        }
    }

    private interface MarkToDeleteFileListener {
        void addToDeleted(DuplicateFile duplicateFile);

        void removeToDeleted(DuplicateFile duplicateFile);
    }
}
