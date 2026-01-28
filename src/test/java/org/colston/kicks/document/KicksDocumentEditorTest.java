package org.colston.kicks.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KicksDocumentEditorTest {

    @Test
    void remove() {
        KicksDocument original = new KicksDocument();
        original.getNotes().add(new Note(0, 6, 2, 0));
        original.getNotes().add(new Note(1, 6, 2, 1));
        original.getNotes().add(new Note(1, 12, 2, 1));
        original.getNotes().add(new Note(2, 6, 2, 2));
        original.getNotes().add(new Note(5, 6, 2, 0));
        KicksDocument edited = new KicksDocument();
        edited.getNotes().add(new Note(0, 6, 2, 0));
        edited.getNotes().add(new Note(5, 6, 2, 0));
        KicksDocument event = new KicksDocument();
        event.getNotes().add(new Note(1, 6, 2, 1));
        event.getNotes().add(new Note(1, 12, 2, 1));
        event.getNotes().add(new Note(2, 6, 2, 2));

        KicksDocumentEditor editor = new KicksDocumentEditor();
        editor.setDocument(original);
        LocatableRange selection = new LocatableRange();
        selection.set(1, 6, 2, 6);
        editor.remove(selection);
        
        assertEquals(original, edited);
    }
}