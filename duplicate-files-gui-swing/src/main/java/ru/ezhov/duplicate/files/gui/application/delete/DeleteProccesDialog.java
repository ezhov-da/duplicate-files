package ru.ezhov.duplicate.files.gui.application.delete;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

public class DeleteProccesDialog extends JDialog {
    private JLabel labelInfo = new JLabel("Click \"Delete\" to start");
    private JButton buttonDelete = new JButton("Delete");
    private JButton buttonCancel = new JButton("Cancel");

    public DeleteProccesDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle("Deleting Files");
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
