package ru.ezhov.duplicate.files.gui;

import ru.ezhov.duplicate.files.gui.analyse.AnalysePanel;

import javax.swing.*;
import java.awt.*;

public class DuplicateFilesGuiApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }
            try {
                JFrame frame = new JFrame("Дубликаты");
                frame.setIconImage(new ImageIcon(DuplicateFilesGuiApplication.class.getResource("/duplicate_16x16.png")).getImage());
                frame.add(new AnalysePanel(), BorderLayout.CENTER);
                frame.setSize(1000, 600);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
