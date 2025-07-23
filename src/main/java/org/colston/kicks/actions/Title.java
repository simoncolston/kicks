package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.Tuning;
import org.colston.kicks.gui.util.JapaneseTextFocusListener;
import org.colston.lib.i18n.Messages;
import org.colston.utils.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Title extends AbstractAction {
    public static final String ACTION_COMMAND = "action.title";

    private static final String MESSAGE_RESOURCE_PREFIX = "title";
    private JDialog dialog;

    public Title() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        KicksDocument doc = KicksApp.canvas().getDocument();
        dialog = new JDialog(KicksApp.frame(), Messages.get(Title.class, "title.dialog.title"), true);

        JTextField titleText = new JTextField(doc.getTitle(), 30);
        titleText.addFocusListener(new JapaneseTextFocusListener());
        Tuning[] tunings = Tuning.values();
        JComboBox<Tuning> tuningCombo = new JComboBox<>(tunings);
        tuningCombo.setSelectedItem((doc.getTuning() != null) ? doc.getTuning() : Tuning.HONCHOUSHI);
        tuningCombo.setRenderer(new TuningListCellRenderer());
        JTextField transcriptionText = new JTextField(doc.getProperties().getTranscription(), 30);

        JPanel inputPanel = new JPanel(new SpringLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel label = new JLabel(Messages.get(Title.class, "title.dialog.title.prompt"));
        inputPanel.add(label);
        label.setLabelFor(titleText);
        inputPanel.add(titleText);

        label = new JLabel(Messages.get(Title.class, "title.dialog.tuning.prompt"));
        inputPanel.add(label);
        label.setLabelFor(tuningCombo);
        inputPanel.add(tuningCombo);

        label = new JLabel(Messages.get(Title.class, "title.dialog.transcription.prompt"));
        inputPanel.add(label);
        label.setLabelFor(transcriptionText);
        inputPanel.add(transcriptionText);

        SpringUtilities.makeCompactGrid(inputPanel, 3, 2, 5, 5, 5, 5);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        buttonPanel.add(Box.createHorizontalGlue());
        JButton b = new JButton(Messages.get(Title.class, "title.dialog.ok"));
        b.addActionListener(actionEvent -> {
            // TODO: These should really be one edit!
            KicksApp.canvas().getEditor().setTitle(titleText.getText());
            KicksApp.canvas().getEditor().setTuning((Tuning) tuningCombo.getSelectedItem());
            KicksApp.canvas().getEditor().setTranscription(transcriptionText.getText());
            dialog.setVisible(false);
        });
        buttonPanel.add(b);
        dialog.getRootPane().setDefaultButton(b);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        b = new JButton(Messages.get(Title.class, "title.dialog.cancel"));
        b.addActionListener(actionEvent -> dialog.setVisible(false));
        buttonPanel.add(b);

        JPanel content = new JPanel(new BorderLayout());
        content.add(inputPanel, BorderLayout.NORTH);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(KicksApp.frame());

        dialog.setVisible(true);
    }

    private static class TuningListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText(((Tuning) value).getDisplayName());
            return label;
        }
    }
}
