package org.colston.kicks.document;

import java.util.Objects;

public class SimpleLocatable implements Locatable {

    private int index = -1;
    private int offset = -1;

    public SimpleLocatable() {
    }

    public SimpleLocatable(int index, int offset) {
        this.index = index;
        this.offset = offset;
    }

    public SimpleLocatable(Locatable locatable) {
        this.index = locatable.getIndex();
        this.offset = locatable.getOffset();
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void clear() {
        index = offset = -1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleLocatable that = (SimpleLocatable) o;
        return index == that.index && offset == that.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, offset);
    }

    @Override
    public String toString() {
        return "SimpleLocatable{" +
                "index=" + index +
                ", offset=" + offset +
                '}';
    }
}
