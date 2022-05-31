package org.colston.kicks.gui.canvas;

import org.colston.kicks.Settings;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.gui.util.JapaneseTextFocusListener;
import org.colston.sclib.i18n.Messages;
import org.colston.utils.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.im.InputContext;

public final class CanvasFactory {
    public static Canvas create() {
        JTextField text = new JTextField();
        text.setBorder(BorderFactory.createLineBorder(CanvasPanel.CURSOR_COLOUR));
        text.enableInputMethods(true);
        text.addFocusListener(new JapaneseTextFocusListener());

        CanvasPanel canvas = new CanvasPanel(text);
        canvas.addFocusListener(new CanvasPanelFocusListener());

        JPanel container = new JPanel();
        container.setBackground(Color.GRAY);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(Box.createVerticalStrut(10));
        container.add(canvas);
        container.add(Box.createVerticalStrut(10));

        JPanel inputComponent = new JPanel();
        inputComponent.setLayout(new BoxLayout(inputComponent, BoxLayout.X_AXIS));
        inputComponent.setBackground(Color.GRAY);
        inputComponent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        NoteButtonPanel nbp = new NoteButtonPanel();
        nbp.setAlignmentY(Component.TOP_ALIGNMENT);
        inputComponent.add(Box.createHorizontalGlue());
        inputComponent.add(nbp);
        inputComponent.add(Box.createHorizontalStrut(10));
        JPanel cursorPanel = createCursorControlPanel();
        cursorPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        inputComponent.add(cursorPanel);
        inputComponent.add(Box.createHorizontalGlue());

        CanvasControl control = new CanvasControl(container, canvas, inputComponent);
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

    private static JPanel createCursorControlPanel() {

        JPanel buttons = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton button = createAutoCursorButton("canvas.cursor.auto.off", false);
        buttonGroup.add(button);
        buttons.add(button);
        button = createAutoCursorButton("canvas.cursor.auto.half", false);
        buttonGroup.add(button);
        buttons.add(button);
        button = createAutoCursorButton("canvas.cursor.auto.one", true);
        buttonGroup.add(button);
        buttons.add(button);

        JPanel cursorPanel = new JPanel(new SpringLayout());
        cursorPanel.setBorder(
                BorderFactory.createTitledBorder(Messages.get(CanvasFactory.class, "cursor.panel.title")));
        JLabel label = new JLabel(Messages.get(CanvasFactory.class, "cursor.panel.step.size"));
        label.setToolTipText(Messages.get(CanvasFactory.class, "cursor.panel.step.size.tooltip"));
        cursorPanel.add(label);
        label.setLabelFor(buttons);
        cursorPanel.add(buttons);
        SpringUtilities.makeCompactGrid(cursorPanel, 1, 2, 5, 5, 5, 5);

        cursorPanel.setMaximumSize(cursorPanel.getMinimumSize());

        return cursorPanel;
    }

    private static JRadioButton createAutoCursorButton(String actionName, boolean selectedState) {
        CanvasAction ca = CanvasActions.getAction(actionName);
        ca.putValue(Action.SELECTED_KEY, selectedState);
        JRadioButton button = new JRadioButton(ca);
        // setting this one to selected and the ButtonGroup handles the others in the group
        ca.setListener(e -> button.setSelected(true));
        button.setFocusable(false);
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
