package ru.ezhov.duplicate.files.gui.application.analyse.result;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import ru.ezhov.duplicate.files.gui.application.MarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.UnmarkToDeleteListener;
import ru.ezhov.duplicate.files.gui.application.analyse.result.domain.DuplicateFile;
import ru.ezhov.duplicate.files.gui.application.analyse.result.domain.Md5Hash;
import ru.ezhov.duplicate.files.gui.application.delete.UploadPreparedDeleteFileListener;
import ru.ezhov.duplicate.files.gui.application.repository.ThumbnailsRepository;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.DuplicateId;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;
import ru.ezhov.duplicate.files.stamp.analyzer.model.service.FilePath;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class AnalyseResultTreeTablePanel extends JPanel implements UnmarkToDeleteListener, MarkToDeleteListener, UploadPreparedDeleteFileListener {
    private static final int COUNT_DUPLICATE_FILE_ON_PAGE = 100;
    private List<UnmarkToDeleteListener> unmarkToDeleteListeners = new ArrayList<>();
    private List<MarkToDeleteListener> markToDeleteListeners = new ArrayList<>();
    private JPanel panelPaginator;
    private java.util.List<Map.Entry<DuplicateId, java.util.List<FilePath>>> entries;
    private JXTreeTable treeTable;
    private DefaultTreeTableModel treeTableModel;
    private ThumbnailsRepository thumbnailsRepository;
    private Map<String, FilePath> cacheFilePaths = new HashMap<>();
    private Map<String, DuplicateFile> cacheDuplicateFiles = new HashMap<>();

    public AnalyseResultTreeTablePanel(Map<DuplicateId, List<FilePath>> map, ThumbnailsRepository thumbnailsRepository) {
        this.thumbnailsRepository = thumbnailsRepository;
        setLayout(new BorderLayout());
        entries = new ArrayList<>();
        for (Map.Entry<DuplicateId, java.util.List<FilePath>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                entries.add(entry);
            }
        }

        for (Map.Entry<DuplicateId, List<FilePath>> entry : entries) {
            for (FilePath filePath : entry.getValue()) {
                DuplicateFile duplicateFile = new DuplicateFile(filePath);
                cacheDuplicateFiles.put(filePath.path(), duplicateFile);
            }
        }

        java.util.List<Map.Entry<DuplicateId, java.util.List<FilePath>>> part = entries.subList(0, entries.size() > COUNT_DUPLICATE_FILE_ON_PAGE ? COUNT_DUPLICATE_FILE_ON_PAGE : entries.size());
        treeTableModel = createFrom(part);
        treeTable = new JXTreeTable(treeTableModel);

        int pages = (int) Math.ceil(entries.size() / Double.parseDouble(COUNT_DUPLICATE_FILE_ON_PAGE + ""));

        panelPaginator = new JPanel();
        JSpinner spinnerPage = new JSpinner(new SpinnerNumberModel(1, 1, pages, 1));
        JLabel labelAllPages = new JLabel(pages + "");
        panelPaginator.add(spinnerPage);
        panelPaginator.add(labelAllPages);

        spinnerPage.addChangeListener(e -> changePage(spinnerPage));

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
                            Desktop.getDesktop().open(duplicateFile.getFile());
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
                            duplicateFile.unmarkDeleted();
                            fireUnmarkToDelete(duplicateFile.getPath());
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                treeTable.repaint();
                            });
                            duplicateFile.markDeleted();
                            fireMarkToDelete(duplicateFile.getPath());
                        }
                    }
                }
            }
        });

        initTreeTable(treeTable);
        add(new MarkedAfterFirst(), BorderLayout.NORTH);
        add(new JScrollPane(treeTable), BorderLayout.CENTER);
        add(panelPaginator, BorderLayout.SOUTH);
    }

    private void changePage(JSpinner spinner) {
        int pageNumber = Integer.parseInt(spinner.getModel().getValue() + "");
        java.util.List<Map.Entry<DuplicateId, java.util.List<FilePath>>> part;
        if (pageNumber - 1 == 0) {
            part = entries.subList(0, entries.size() > COUNT_DUPLICATE_FILE_ON_PAGE ? COUNT_DUPLICATE_FILE_ON_PAGE : entries.size());
        } else {
            int pnStart = (pageNumber - 1) * COUNT_DUPLICATE_FILE_ON_PAGE;
            int pnEnd = pageNumber * COUNT_DUPLICATE_FILE_ON_PAGE;
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
                    label.setIcon(new ImageIcon(thumbnailsRepository.by(duplicateFile.getPath())));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
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

    private DefaultTreeTableModel createFrom(java.util.List<Map.Entry<DuplicateId, java.util.List<FilePath>>> entries) {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
        for (Map.Entry<DuplicateId, List<FilePath>> entry : entries) {
            DefaultMutableTreeTableNode hashNode = new DefaultMutableTreeTableNode(new Md5Hash(entry.getKey()));
            for (FilePath filePath : entry.getValue()) {
                DuplicateFile duplicateFile;
                if (cacheDuplicateFiles.containsKey(filePath.path())) {
                    duplicateFile = cacheDuplicateFiles.get(filePath.path());
                } else {
                    duplicateFile = new DuplicateFile(filePath);
                    cacheDuplicateFiles.put(filePath.path(), duplicateFile);
                }
                hashNode.add(new DefaultMutableTreeTableNode(duplicateFile));
            }
            root.add(hashNode);
        }

        recursiveMarkedByCacheFilePaths(root);

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

    @Override
    public void mark(FilePath filePath) {
        //TODO: возможно не нужно
    }

    @Override
    public void unmark(FilePath filePath) {
        if (cacheDuplicateFiles.containsKey(filePath.path())) {
            DuplicateFile duplicateFile = cacheDuplicateFiles.get(filePath.path());
            duplicateFile.unmarkDeleted();
        }

        SwingUtilities.invokeLater(() -> {
            treeTable.repaint();
        });
    }

    @Override
    public void upload(List<FilePath> filePaths) {
        for (FilePath filePath : filePaths) {
            if (!cacheFilePaths.containsKey(filePath.path())) {
                cacheFilePaths.put(filePath.path(), filePath);
            }
        }

        SwingUtilities.invokeLater(() -> {
            DefaultMutableTreeTableNode root = (DefaultMutableTreeTableNode) treeTableModel.getRoot();
            recursiveMarkedByCacheFilePaths(root);
            treeTable.repaint();

        });
    }

    private void recursiveMarkedByCacheFilePaths(DefaultMutableTreeTableNode mutableTreeTableNode) {
        Object value = mutableTreeTableNode.getValueAt(3);
        if (value instanceof DuplicateFile) {
            DuplicateFile duplicateFile = (DuplicateFile) value;
            if (cacheFilePaths.containsKey(duplicateFile.getPath().path())) {
                duplicateFile.markDeleted();
            }
        }
        Enumeration<? extends MutableTreeTableNode> children = mutableTreeTableNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) children.nextElement();
            recursiveMarkedByCacheFilePaths(node);
        }
    }

    private void recursiveMarkedAllButTheFirst(DefaultMutableTreeTableNode mutableTreeTableNode) {
        Object value = mutableTreeTableNode.getValueAt(3);
        if (value instanceof Md5Hash) {
            Enumeration<? extends MutableTreeTableNode> children = mutableTreeTableNode.children();
            int counter = 1;
            while (children.hasMoreElements()) {
                MutableTreeTableNode element = children.nextElement();
                value = element.getValueAt(3);
                if (counter == 1) {
                    if (value instanceof DuplicateFile) {
                        DuplicateFile duplicateFile = (DuplicateFile) value;
                        duplicateFile.unmarkDeleted();
                    }
                    counter++;
                    continue;
                }
                if (value instanceof DuplicateFile) {
                    DuplicateFile duplicateFile = (DuplicateFile) value;
                    duplicateFile.markDeleted();
                    fireMarkToDelete(duplicateFile.getPath());
                }
                counter++;
            }
        }
        Enumeration<? extends MutableTreeTableNode> children = mutableTreeTableNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) children.nextElement();
            recursiveMarkedAllButTheFirst(node);
        }
    }

    private class MarkedAfterFirst extends JPanel {
        private JButton buttonMark = new JButton("Подготовить для удаления все файлы на этой странице, кроме первой в каждой группе");

        public MarkedAfterFirst() {
            BoxLayout boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);

            setLayout(boxLayout);
            add(Box.createHorizontalGlue());
            add(buttonMark);

            buttonMark.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    DefaultMutableTreeTableNode root = (DefaultMutableTreeTableNode) treeTableModel.getRoot();
                    recursiveMarkedAllButTheFirst(root);
                    treeTable.repaint();
                });
            });
        }
    }
}
