package ru.ezhov.duplicate.files.gui.application.stamp;

import javax.swing.*;
import java.awt.*;

public class StatisticPanelIT {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }
            JFrame frame = new JFrame("_________");
            frame.setSize(1000, 200);
            frame.add(new StampPanel(), BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}