package org.colston.kicks.gui.canvas;

import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.KicksDocumentEditor;

class CanvasModel {

    private final KicksDocumentEditor editor = new KicksDocumentEditor();

    public KicksDocumentEditor getEditor() {
        return editor;
    }

    public KicksDocument getDocument() {
        return editor.getDocument();
    }

    public void setDocument(KicksDocument document) {
        editor.setDocument(document);
    }
}
