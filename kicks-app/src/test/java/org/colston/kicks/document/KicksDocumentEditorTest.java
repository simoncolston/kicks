package org.colston.kicks.document;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class KicksDocumentEditorTest {

    private static KicksDocument unedited;
    private static KicksDocument editedEmpty;
    private static KicksDocument editedA;
    private static KicksDocument editedB;
    private static KicksDocument editedC;

    @BeforeAll
    static void init() {
        unedited = new KicksDocument();
        unedited.getNotes().add(new Note(0, 6, 2, 0));
        unedited.getNotes().add(new Note(1, 6, 2, 1));
        unedited.getNotes().add(new Note(1, 12, 2, 1));
        unedited.getNotes().add(new Note(2, 6, 2, 2));
        unedited.getNotes().add(new Note(5, 6, 2, 0));

        editedEmpty = new KicksDocument();
        editedA = new KicksDocument();
        editedA.getNotes().add(new Note(0, 6, 2, 0));
        editedA.getNotes().add(new Note(5, 6, 2, 0));
        editedB = new KicksDocument();
        editedB.getNotes().add(new Note(5, 6, 2, 0));
        editedC = new KicksDocument();
        editedC.getNotes().add(new Note(1, 6, 2, 1));
        editedC.getNotes().add(new Note(1, 12, 2, 1));
        editedC.getNotes().add(new Note(2, 6, 2, 2));
        editedC.getNotes().add(new Note(5, 6, 2, 0));
    }

    @Test
    void removeFromEmptyDocument() {
        KicksDocument original = new KicksDocument();
        KicksDocument edited = new KicksDocument();

        KicksDocumentEditor editor = new KicksDocumentEditor();
        editor.setDocument(original);
        SimpleLocatableRange selection = new SimpleLocatableRange();
        selection.set(1, 6, 2, 6);
        editor.remove(selection);

        assertEquals(original, edited);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForRemoveFromDocument")
    void removeFromDocument(LocatableRange selection, KicksDocument edited) {
        KicksDocument original = new KicksDocument();
        original.getNotes().add(new Note(0, 6, 2, 0));
        original.getNotes().add(new Note(1, 6, 2, 1));
        original.getNotes().add(new Note(1, 12, 2, 1));
        original.getNotes().add(new Note(2, 6, 2, 2));
        original.getNotes().add(new Note(5, 6, 2, 0));

        KicksDocumentEditor editor = new KicksDocumentEditor();
        editor.setDocument(original);
        editor.remove(selection);

        assertEquals(original, edited);
    }

    private static Stream<Arguments> provideParamsForRemoveFromDocument() {
        return Stream.of(
                // start and end on a note
                Arguments.of(new SimpleLocatableRange(1, 6, 2, 6), editedA),
                // start before first note, end on a note
                Arguments.of(new SimpleLocatableRange(0, 5, 2, 6), editedB),
                // start between first and second note, end on a note
                Arguments.of(new SimpleLocatableRange(1, 5, 2, 6), editedA),
                // delete all
                Arguments.of(new SimpleLocatableRange(0, 0, 6, 12), editedEmpty),
                // delete nothing, in first square though
                Arguments.of(new SimpleLocatableRange(0, 0, 0, 1), unedited),
                // remove single note only
                Arguments.of(new SimpleLocatableRange(0, 6, 0, 7), editedC)
        );
    }
}