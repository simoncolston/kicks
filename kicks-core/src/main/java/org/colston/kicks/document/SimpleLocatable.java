package org.colston.kicks.document;

public class SimpleLocatable extends AbstractLocatable {

    public SimpleLocatable() {
        super(-1, -1);
    }

    public SimpleLocatable(int index, int offset) {
        super(index, offset);
    }

    public SimpleLocatable(Locatable locatable) {
        super(locatable.getIndex(),  locatable.getOffset());
    }

    @Override
    public void setIndex(int index) {
        super.setIndex(index);
    }

    @Override
    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    public void clear() {
        setIndex(-1);
        setOffset(-1);
    }
}
