package org.colston.kicks.document;

import javax.swing.undo.AbstractUndoableEdit;

public class KicksDocumentEdit extends AbstractUndoableEdit implements Locatable {
    private int index;
    private int offset;

    private boolean updateLocation = true;

    private String presentationName;

    public KicksDocumentEdit(int index, int offset, String presentationName) {
        this.index = index;
        this.offset = offset;
        this.presentationName = presentationName;
    }

    public KicksDocumentEdit(String presentationName) {
        this.updateLocation = false;
        this.presentationName = presentationName;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    public boolean isUpdateLocation() {
        return updateLocation;
    }

    @Override
    public String getPresentationName() {
        return presentationName;
    }
}
