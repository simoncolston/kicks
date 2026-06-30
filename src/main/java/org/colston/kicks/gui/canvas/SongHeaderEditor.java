package org.colston.kicks.gui.canvas;

import org.colston.kicks.KicksApp;
import org.colston.kicks.document.Song;
import org.colston.kicks.document.Tuning;
import org.colston.kicks.gui.util.JapaneseTextFocusListener;
import org.colston.lib.i18n.Messages;
import org.colston.utils.SpringUtilities;

import javax.swing.*;
import java.awt.*;

class SongHeaderEditor {

    private JDialog dialog;

    void edit(Song song, int cursorIndex, int cursorOffset) {
        dialog = new JDialog(KicksApp.frame(), Messages.get(SongHeaderEditor.class, "song.header.dialog.title"), true);

        JTextField titleText = new JTextField(song.getTitle(), 30);
        titleText.addFocusListener(new JapaneseTextFocusListener());
        Tuning[] tunings = Tuning.values();
        JComboBox<Tuning> tuningCombo = new JComboBox<>(tunings);
        tuningCombo.setSelectedItem((song.getTuning() != null) ? song.getTuning() : Tuning.HONCHOUSHI);
        tuningCombo.setRenderer(new TuningListCellRenderer());
        JTextField transcriptionText = new JTextField(song.getTranscription(), 30);

        JPanel inputPanel = new JPanel(new SpringLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel label = new JLabel(Messages.get(SongHeaderEditor.class, "song.header.dialog.title.prompt"));
        inputPanel.add(label);
        label.setLabelFor(titleText);
        inputPanel.add(titleText);

        label = new JLabel(Messages.get(SongHeaderEditor.class, "song.header.dialog.tuning.prompt"));
        inputPanel.add(label);
        label.setLabelFor(tuningCombo);
        inputPanel.add(tuningCombo);

        label = new JLabel(Messages.get(SongHeaderEditor.class, "song.header.dialog.transcription.prompt"));
        inputPanel.add(label);
        label.setLabelFor(transcriptionText);
        inputPanel.add(transcriptionText);

        SpringUtilities.makeCompactGrid(inputPanel, 3, 2, 5, 5, 5, 5);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        buttonPanel.add(Box.createHorizontalGlue());
        JButton b = new JButton(Messages.get(SongHeaderEditor.class, "song.header.dialog.ok"));
        b.addActionListener(actionEvent -> {
            Song s = new Song(song.getIndex());
            s.setTitle(titleText.getText());
            s.setTuning((Tuning) tuningCombo.getSelectedItem());
            s.setTranscription(transcriptionText.getText());
            KicksApp.canvas().getEditor().addSong(s, cursorIndex, cursorOffset);
            dialog.setVisible(false);
        });
        buttonPanel.add(b);
        dialog.getRootPane().setDefaultButton(b);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        b = new JButton(Messages.get(SongHeaderEditor.class, "song.header.dialog.cancel"));
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
