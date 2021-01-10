package org.colston.kicks.gui.canvas;

import org.colston.gui.task.TaskPanel;
import org.colston.kicks.KicksMain;
import org.colston.kicks.Settings;
import org.colston.kicks.document.KicksDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.im.InputContext;
import java.util.Locale;

public final class CanvasFactory {
    public static Canvas create() {
        JTextField text = new JTextField();
        text.setBorder(BorderFactory.createLineBorder(CanvasPanel.CURSOR_COLOUR));
        text.enableInputMethods(true);
        text.addFocusListener(new TextFocusListener());

        CanvasPanel canvas = new CanvasPanel(text);
        canvas.addFocusListener(new CanvasPanelFocusListener());

        JPanel container = new JPanel();
        container.setBackground(Color.GRAY);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(Box.createVerticalStrut(10));
        TaskPanel taskPanel = new TaskPanel(canvas);
        container.add(taskPanel);
        container.add(Box.createVerticalStrut(10));

        JPanel inputComponent = new JPanel();
        inputComponent.setBackground(Color.GRAY);
        NoteButtonPanel nbp = new NoteButtonPanel();
        inputComponent.add(nbp);

        CanvasControl control = new CanvasControl(container, canvas, inputComponent);
        control.setDocument(new KicksDocument());

        /*
         * Initialise and set up actions on components.
         */
        CanvasActions.initialise(control);

        CanvasActions.addPrefixToInputActionMaps(canvas, "canvas.");

        CanvasActions.addPrefixToInputActionMaps(text, "canvas.cursor.");
        CanvasActions.addPrefixToInputActionMaps(text, "canvastext.");

        return control;
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

    private static class TextFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            InputContext ic = e.getComponent().getInputContext();
            ic.selectInputMethod(Locale.JAPAN);
            ic.setCharacterSubsets(KicksMain.getSettings().getCharacterSubset());
        }

        @Override
        public void focusLost(FocusEvent e) {
        }
    }
}
