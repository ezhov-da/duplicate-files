package ru.ezhov.duplicate.files.gui;

import javax.swing.*;
import java.awt.*;

public class DuplicateFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }
            JFrame frame = new JFrame("Дубликаты");
            frame.add(createBasicPanel(), BorderLayout.CENTER);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private static JPanel createBasicPanel() {
        JPanel panel = new JPanel();

        return panel;
    }
}
