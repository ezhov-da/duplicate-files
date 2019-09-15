package ru.ezhov.duplicate.files.gui.application.delete;

import com.sun.org.apache.xerces.internal.impl.dv.xs.AbstractDateTimeDV;

import javax.swing.*;
import java.awt.*;

public class DeleteProccesDialog extends JDialog {
    private JLabel labelInfo = new JLabel("Нажмите \"Удалить\" для начала удаления");
    private JButton buttonDelete = new JButton("Удалить");
    private JButton buttonCancel = new JButton("Отменить");

    public DeleteProccesDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle("Удаление файлов");
        labelInfo.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelInfo, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonDelete, BorderLayout.CENTER);
        panel.add(buttonCancel, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);

        setSize(700, 200);
        setLocationRelativeTo(null);
    }

    public void showWith(Runnable runnable) {
        labelInfo.setText("");
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        Runnable runnableInner = () -> {
            setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            runnable.run();
        };

        buttonDelete.addActionListener(e -> runnableInner.run());
        this.setVisible(true);
    }

    public void cancel(Runnable runnable) {
        buttonCancel.addActionListener(e -> runnable.run());
    }

    public void publishInfo(String text) {
        labelInfo.setText(text);
    }
}
