package ru.ezhov.duplicate.files.gui.analyse;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import ru.ezhov.duplicate.files.core.stamp.generator.service.stamp.analyzer.service.DuplicateFilesAnalyserService;
import ru.ezhov.duplicate.files.gui.analyse.domain.DuplicateFile;
import ru.ezhov.duplicate.files.gui.analyse.domain.Md5Hash;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnalyseResultTreeTablePanel extends JPanel {

    private List<MarkToDeleteFileListener> markToDeleteFileListeners = new ArrayList<>();
    private JPanel panelPaginator;

    public AnalyseResultTreeTablePanel(Map<String, List<String>> map) {
        setLayout(new BorderLayout());
        java.util.List<Map.Entry<String, java.util.List<String>>> entries = new ArrayList<>();
        for (Map.Entry<String, java.util.List<String>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                entries.add(entry);
            }
        }
        java.util.List<Map.Entry<String, java.util.List<String>>> part = entries.subList(0, entries.size() > 10 ? 10 : entries.size());
        final TreeTableModel[] treeTableModel = {createFrom(part)};
        JXTreeTable treeTable = new JXTreeTable(treeTableModel[0]);

        int pages = (int) Math.ceil(entries.size() / 10D);

        panelPaginator = new JPanel();
        JSpinner spinnerPage = new JSpinner(new SpinnerNumberModel(1, 1, pages, 1));
        JLabel labelAllPages = new JLabel(pages + "");
        panelPaginator.add(spinnerPage);
        panelPaginator.add(labelAllPages);
        ((JSpinner.DefaultEditor) spinnerPage.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    return;
                }
                int pageNumber = Integer.valueOf(spinnerPage.getModel().getValue() + "");
                java.util.List<Map.Entry<String, java.util.List<String>>> part;
                if (pageNumber - 1 == 0) {
                    part = entries.subList(0, entries.size() > 10 ? 10 : entries.size());
                } else {
                    int pnStart = (pageNumber - 1) * 10;
                    int pnEnd = pageNumber * 10;
                    part = entries.subList(pnStart, entries.size() > pnEnd ? pnEnd : entries.size());
                }
                treeTableModel[0] = createFrom(part);
                SwingUtilities.invokeLater(() -> {
                    treeTable.setTreeTableModel(treeTableModel[0]);
                    initTreeTable(treeTable);
                });
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
                                actionRemoveMark(duplicateFile.getPath());
                                treeTable.repaint();
                            });
                            duplicateFile.setMarkDeleted(false);
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                actionMark(duplicateFile.getPath());
                                treeTable.repaint();
                                duplicateFile.setMarkDeleted(true);
                            });
                        }
                    }
                }
            }
        });

        initTreeTable(treeTable);
        add(new JScrollPane(treeTable), BorderLayout.CENTER);
        add(panelPaginator, BorderLayout.SOUTH);
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
                //TODO: откорректировать
                //duplicateFile.setMarkDeleted(duplicateFilesToRemovePanel.isMarkAsDeleted(filePath));
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

    private void actionMark(String filePath) {
        markToDeleteFileListeners.forEach(ml -> ml.mark(filePath));
    }

    private void actionRemoveMark(String filePath) {
        markToDeleteFileListeners.forEach(ml -> ml.removeMark(filePath));
    }

    public void addMarkToDeleteFileListener(MarkToDeleteFileListener markToDeleteFileListener) {
        markToDeleteFileListeners.add(markToDeleteFileListener);
    }

}
