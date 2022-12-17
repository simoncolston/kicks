package org.colston.kicks.gui.canvas;

import org.colston.kicks.Settings;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.gui.util.JapaneseTextFocusListener;

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

        InputComponent inputComponent = new InputComponent(canvas, model);

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
