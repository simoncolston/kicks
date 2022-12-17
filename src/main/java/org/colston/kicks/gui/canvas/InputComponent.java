package org.colston.kicks.gui.canvas;

import org.colston.kicks.KicksApp;
import org.colston.kicks.document.Note;
import org.colston.lib.i18n.Messages;

import javax.swing.*;
import java.awt.*;

public class InputComponent extends JPanel {
    private JRadioButton smallNoteRadioButton;

    InputComponent(CanvasPanel canvas, CanvasModel model) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(KicksApp.PANEL_COLOUR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        NoteButtonPanel noteButtonPanel = new NoteButtonPanel();
        noteButtonPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel editButtonPanel = createEditButtonPanel();
        editButtonPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel noteSizePanel = createNoteSizePanel(canvas, model);
        noteSizePanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel cursorControlPanel = createCursorControlPanel();
        cursorControlPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        add(Box.createHorizontalGlue());
        add(editButtonPanel);
        add(Box.createHorizontalStrut(10));
        add(noteButtonPanel);
        add(Box.createHorizontalStrut(10));
        add(noteSizePanel);
        add(Box.createHorizontalStrut(10));
        add(cursorControlPanel);
        add(Box.createHorizontalGlue());
    }

    boolean isSmallNoteSelected() {
        return smallNoteRadioButton.isSelected();
    }

    private JPanel createNoteSizePanel(CanvasPanel canvas, CanvasModel model) {
        JPanel noteSizePanel = new JPanel();
        noteSizePanel.setLayout(new BoxLayout(noteSizePanel, BoxLayout.Y_AXIS));
        noteSizePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(Messages.get(CanvasFactory.class, "note.size.panel.title")),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton normalButton = createRadioButton("canvas.note.size.normal", true);
        buttonGroup.add(normalButton);
        noteSizePanel.add(normalButton);
        smallNoteRadioButton = createRadioButton("canvas.note.size.small", false);
        buttonGroup.add(smallNoteRadioButton);
        noteSizePanel.add(smallNoteRadioButton);
        noteSizePanel.setAlignmentY(Component.TOP_ALIGNMENT);

        canvas.addListener((index, offset) -> {
            Note note = model.getDocument().getNote(index, offset);
            JRadioButton button = normalButton;
            if (note != null) {
                if (note.isSmall()) {
                    button = smallNoteRadioButton;
                }
            } else if (offset == 0 || offset == CanvasPanel.CELL_TICKS) {
                button = smallNoteRadioButton;
            }
            button.setSelected(true);
        });
        return noteSizePanel;
    }

    private JPanel createEditButtonPanel() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JButton button = new JButton(CanvasActions.getAction("canvas.slur"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("Slur.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        leftPanel.add(button);

        button = new JButton(CanvasActions.getAction("canvas.chord"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("Chord.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        leftPanel.add(button);

        JPanel repeatPanel = new JPanel();
        repeatPanel.setOpaque(false);
        repeatPanel.setLayout(new BoxLayout(repeatPanel, BoxLayout.Y_AXIS));

        button = new JButton(CanvasActions.getAction("canvas.repeatstart"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("RepeatStart.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        repeatPanel.add(button);

        button = new JButton(CanvasActions.getAction("canvas.repeatend"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("RepeatEnd.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        repeatPanel.add(button);

        JPanel restPanel = new JPanel();
        restPanel.setOpaque(false);
        restPanel.setLayout(new BoxLayout(restPanel, BoxLayout.Y_AXIS));

        button = new JButton(CanvasActions.getAction("canvas.rest"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("Rest.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        restPanel.add(button);

        button = new JButton(CanvasActions.getAction("canvas.flat"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("Flat.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        restPanel.add(button);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        button = new JButton(CanvasActions.getAction("canvas.utou.uchi"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("UchiUtou.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        rightPanel.add(button);

        button = new JButton(CanvasActions.getAction("canvas.utou.kaki"));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("KakiUtou.png"));
        button.setMargin(new Insets(2, 2, 2, 2));

        rightPanel.add(button);

        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(leftPanel);
        repeatPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(repeatPanel);
        restPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(restPanel);
        rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(rightPanel);
        return panel;
    }

    private JPanel createCursorControlPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(Messages.get(CanvasFactory.class, "cursor.panel.title")));

        JPanel autoCursorButtonPanel = new JPanel();
        autoCursorButtonPanel.setLayout(new BoxLayout(autoCursorButtonPanel, BoxLayout.Y_AXIS));
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton button = createRadioButton("canvas.cursor.auto.off", false);
        buttonGroup.add(button);
        autoCursorButtonPanel.add(button);
        button = createRadioButton("canvas.cursor.auto.half", false);
        buttonGroup.add(button);
        autoCursorButtonPanel.add(button);
        button = createRadioButton("canvas.cursor.auto.one", true);
        buttonGroup.add(button);
        autoCursorButtonPanel.add(button);

        JPanel cursorStepPanel = new JPanel();
        cursorStepPanel.setLayout(new BoxLayout(cursorStepPanel, BoxLayout.X_AXIS));
        cursorStepPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel label = new JLabel(Messages.get(CanvasFactory.class, "cursor.panel.step.size"));
        label.setAlignmentY(Component.TOP_ALIGNMENT);
        label.setToolTipText(Messages.get(CanvasFactory.class, "cursor.panel.step.size.tooltip"));
        label.setLabelFor(autoCursorButtonPanel);
        label.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 3));
        cursorStepPanel.add(label);
        autoCursorButtonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        cursorStepPanel.add(autoCursorButtonPanel);

        JPanel p = createCursorMovePanel();
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setMaximumSize(new Dimension(3 * 24 + 30, 2 * 24)); //TODO: don't like this but it works :-(
        panel.add(p);
        cursorStepPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cursorStepPanel);
        panel.add(Box.createRigidArea(new Dimension(20, 20))); // Vertical strut expands horizontally!

        return panel;
    }

    private JPanel createCursorMovePanel() {

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JButton b = new JButton(CanvasActions.getAction("canvas.cursor.up"));
        b.setFocusable(false);
        b.setMargin(new Insets(2, 2, 2, 2));

        c.gridx = 1;
        buttonPanel.add(b, c);

        b = new JButton(CanvasActions.getAction("canvas.cursor.left"));
        b.setFocusable(false);
        b.setMargin(new Insets(2, 2, 2, 2));

        c.gridy = 1;
        c.gridx = 0;
        buttonPanel.add(b, c);

        b = new JButton(CanvasActions.getAction("canvas.cursor.down"));
        b.setFocusable(false);
        b.setMargin(new Insets(2, 2, 2, 2));

        c.gridx = 1;
        buttonPanel.add(b, c);

        b = new JButton(CanvasActions.getAction("canvas.cursor.right"));
        b.setFocusable(false);
        b.setMargin(new Insets(2, 2, 2, 2));

        c.gridx = 2;
        buttonPanel.add(b, c);

        return buttonPanel;
    }

    private static JRadioButton createRadioButton(String actionName, boolean selectedState) {
        CanvasAction ca = CanvasActions.getAction(actionName);
        ca.putValue(Action.SELECTED_KEY, selectedState);
        JRadioButton button = new JRadioButton(ca);
        // setting this one to selected and the ButtonGroup handles the others in the group
        ca.setListener(e -> {if (!button.isSelected()) {button.setSelected(true);}});
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("RadioButtonUnchecked18.png"));
        button.setSelectedIcon(CanvasResources.getIcon("RadioButtonChecked18.png"));
        return button;
    }
}
