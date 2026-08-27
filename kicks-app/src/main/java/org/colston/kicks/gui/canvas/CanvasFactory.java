package org.colston.kicks.gui.canvas;

import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.Song;
import org.colston.kicks.render.PageRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.print.Printable;

public final class CanvasFactory {

    public static Canvas create() {

        CanvasModel model = new CanvasModel();
        CanvasCursorModel cursorModel = new CanvasCursorModel(model);
        CanvasZoomModel zoomModel = new CanvasZoomModel();

        JPanel canvasContainer = new JPanel();
        canvasContainer.setBackground(Color.GRAY);
        canvasContainer.setLayout(new BoxLayout(canvasContainer, BoxLayout.Y_AXIS));
        canvasContainer.add(Box.createVerticalStrut(CanvasPages.PAGE_PADDING));

        CanvasPages canvasPages = CanvasPagesImpl.create(canvasContainer, model, cursorModel, zoomModel);

        InputComponent inputComponent = new InputComponent(model, cursorModel);

        CanvasControl control = new CanvasControl(canvasContainer, canvasPages, model, cursorModel, zoomModel, inputComponent);
        control.setDocument(new KicksDocument(new Song(0)));

        /*
         * Initialise and set up actions on components.
         */
        CanvasActions.initialise(control);

        CanvasActions.addPrefixToInputActionMaps(canvasContainer, "canvas.");

        CanvasActions.enableAll();

        return control;
    }

    public static Printable createPrintable(KicksDocument doc) {
        return PageRenderer.create(doc).romaji(KicksApp.settings().isRomaji());
    }
}
