package ru.ezhov.duplicate.files.gui;

import net.coobird.thumbnailator.Thumbnails;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import ru.ezhov.duplicate.files.AnalyseMd5DuplicateFilesXml;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DuplicateFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }
            try {
                DuplicateFrame duplicateFrame = new DuplicateFrame();
                JFrame frame = new JFrame("Дубликаты");
                frame.add(duplicateFrame.createBasicPanel(), BorderLayout.CENTER);
                frame.setSize(1000, 600);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private JPanel createBasicPanel() throws Exception {
        JPanel panel = new JPanel(new BorderLayout());

        AnalyseMd5DuplicateFilesXml analyseMd5DuplicateFilesXml = new AnalyseMd5DuplicateFilesXml();
//        Map<String, List<String>> map = analyseMd5DuplicateFilesXml.findDuplicate(new File("D:/duplicate-files-terabyte-md5.xml"));
        Map<String, List<String>> map = analyseMd5DuplicateFilesXml.findDuplicate(new File("D:/duplicate-files-md5.xml"));
        List<Map.Entry<String, List<String>>> entries = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                entries.add(entry);
            }
        }

        List<Map.Entry<String, List<String>>> part = entries.subList(0, entries.size() > 10 ? 10 : entries.size());
        final TreeTableModel[] treeTableModel = {createFrom(part)};
        JXTreeTable treeTable = new JXTreeTable(treeTableModel[0]);

        int pages = (int) Math.ceil(entries.size() / 10D);

        JPanel panelPaginator = new JPanel();
        for (int i = 1; i <= pages; i++) {
            JLabel label = new JLabel(i + "");
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            Font font = label.getFont();
            label.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    int pageNumber = Integer.valueOf(label.getText());
                    List<Map.Entry<String, List<String>>> part;
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
            panelPaginator.add(label);
        }

        treeTable.setTreeCellRenderer(new DefaultXTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
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
                        duplicateFile.setMarkDeleted(true);
                        SwingUtilities.invokeLater(treeTable::repaint);
                    }
                }
            }
        });

        initTreeTable(treeTable);

        panel.add(new JScrollPane(treeTable), BorderLayout.CENTER);
        panel.add(panelPaginator, BorderLayout.SOUTH);

        return panel;
    }

    private void initTreeTable(JXTreeTable treeTable) {
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
                    label.setIcon(new ImageIcon(getClass().getResource("/list-add_16x16.png")));
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

    private TreeTableModel createFrom(List<Map.Entry<String, List<String>>> entries) {
        DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode("root");
        for (Map.Entry<String, List<String>> entry : entries) {
            DefaultMutableTreeTableNode hashNode = new DefaultMutableTreeTableNode(new Md5Hash(entry.getKey()));
            for (String filePath : entry.getValue()) {
                hashNode.add(new DefaultMutableTreeTableNode(new DuplicateFile(filePath)));
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

    private class Md5Hash {
        String hash;

        public Md5Hash(String hash) {
            this.hash = hash;
        }

        public String getHash() {
            return hash;
        }

        @Override
        public String toString() {
            return hash;
        }
    }

    private class DuplicateFile {
        private String path;
        private boolean markDeleted;

        private BufferedImage imageIcon;

        public DuplicateFile(String path) {
            this.path = path;
            try {
//                BufferedImage originalImage = ImageIO.read(new File(path));
//                BufferedImage thumbnail = Thumbnails.of(originalImage)
//                        .size(100, 100)
//                        .asBufferedImage();
//                imageIcon = thumbnail;
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
            return markDeleted;
        }

        public void setMarkDeleted(boolean markDeleted) {
            this.markDeleted = markDeleted;
        }

        @Override
        public String toString() {
            return path;
        }
    }
}