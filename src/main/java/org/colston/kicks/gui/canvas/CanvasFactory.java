package org.colston.kicks.gui.canvas;

import org.colston.kicks.KicksApp;
import org.colston.kicks.Settings;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.gui.util.JapaneseTextFocusListener;
import org.colston.sclib.i18n.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.im.InputContext;

public final class CanvasFactory {
    public static Canvas create() {

        CanvasModel model = new CanvasModel();

        JTextField text = new JTextField();
        text.setBorder(BorderFactory.createLineBorder(CanvasPanel.CURSOR_COLOUR));
        text.enableInputMethods(true);
        text.addFocusListener(new JapaneseTextFocusListener());

        CanvasPanel canvas = new CanvasPanel(model, text);
        canvas.addFocusListener(new CanvasPanelFocusListener());

        JPanel canvasContainer = new JPanel();
        canvasContainer.setBackground(Color.GRAY);
        canvasContainer.setLayout(new BoxLayout(canvasContainer, BoxLayout.Y_AXIS));
        canvasContainer.add(Box.createVerticalStrut(10));
        canvasContainer.add(canvas);
        canvasContainer.add(Box.createVerticalStrut(10));

        NoteButtonPanel nbp = new NoteButtonPanel();
        nbp.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel editButtonPanel = createEditButtonPanel();
        editButtonPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel cursorControlPanel = createCursorControlPanel();
        cursorControlPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel inputComponent = new JPanel();
        inputComponent.setLayout(new BoxLayout(inputComponent, BoxLayout.X_AXIS));
        inputComponent.setBackground(KicksApp.PANEL_COLOUR);
        inputComponent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputComponent.add(Box.createHorizontalGlue());
        inputComponent.add(editButtonPanel);
        inputComponent.add(Box.createHorizontalStrut(10));
        inputComponent.add(nbp);
        inputComponent.add(Box.createHorizontalStrut(10));
        inputComponent.add(cursorControlPanel);
        inputComponent.add(Box.createHorizontalGlue());

        CanvasControl control = new CanvasControl(canvasContainer, canvas, model, inputComponent);
        control.setDocument(new KicksDocument());

        /*
         * Initialise and set up actions on components.
         */
        CanvasActions.initialise(control);

        CanvasActions.addPrefixToInputActionMaps(canvas, "canvas.");

        CanvasActions.addPrefixToInputActionMaps(text, "canvas.cursor.");
        CanvasActions.addPrefixToInputActionMaps(text, "canvastext.");

        CanvasActions.enableAll();

        return control;
    }

    private static JPanel createEditButtonPanel() {

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

    private static JPanel createCursorControlPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(Messages.get(CanvasFactory.class, "cursor.panel.title")));

        JPanel autoCursorButtonPanel = new JPanel();
        autoCursorButtonPanel.setLayout(new BoxLayout(autoCursorButtonPanel, BoxLayout.Y_AXIS));
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton button = createAutoCursorButton("canvas.cursor.auto.off", false);
        buttonGroup.add(button);
        autoCursorButtonPanel.add(button);
        button = createAutoCursorButton("canvas.cursor.auto.half", false);
        buttonGroup.add(button);
        autoCursorButtonPanel.add(button);
        button = createAutoCursorButton("canvas.cursor.auto.one", true);
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

    private static JPanel createCursorMovePanel() {

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

    private static JRadioButton createAutoCursorButton(String actionName, boolean selectedState) {
        CanvasAction ca = CanvasActions.getAction(actionName);
        ca.putValue(Action.SELECTED_KEY, selectedState);
        JRadioButton button = new JRadioButton(ca);
        // setting this one to selected and the ButtonGroup handles the others in the group
        ca.setListener(e -> button.setSelected(true));
        button.setFocusable(false);
        button.setIcon(CanvasResources.getIcon("RadioButtonUnchecked18.png"));
        button.setSelectedIcon(CanvasResources.getIcon("RadioButtonChecked18.png"));
        return button;
    }

    private static class CanvasPanelFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            InputContext ic = e.getComponent().getInputContext();
            ic.setCharacterSubsets(Settings.LATIN);
//			ic.selectInputMethod(Locale.getDefault());
        }

        @Override
        public void focusLost(FocusEvent e) {
        }
    }

}
