package org.colston.kicks.document;

import java.util.Objects;

/**
 * Mutable range in a document, from one locatable position to another.
 */
public class SimpleLocatableRange implements LocatableRange {
    private final SimpleLocatable start;
    private final SimpleLocatable end;

    public SimpleLocatableRange(){
        this.start = new SimpleLocatable();
        this.end = new SimpleLocatable();
    }

    public SimpleLocatableRange(SimpleLocatableRange selection) {
        this.start = new SimpleLocatable(selection.start);
        this.end = new SimpleLocatable(selection.end);
    }

    public SimpleLocatableRange(int startIndex, int startOffset, int endIndex, int endOffset) {
        this.start = new SimpleLocatable(startIndex, startOffset);
        this.end = new SimpleLocatable(endIndex, endOffset);
    }

    @Override
    public boolean isEmpty() {
        return start.isEqualTo(end);
    }

    @Override
    public Locatable getLow() {
        return start.isGreaterThan(end) ? end : start;
    }

    @Override
    public Locatable getHigh() {
        return start.isLessThan(end) ? end : start;
    }

    public void set(int startIndex, int startOffset, int endIndex, int endOffset) {
        setStart(startIndex, startOffset);
        setEnd(endIndex, endOffset);
    }

    public void adjust(int index, int offset) {
        setEnd(index, offset);
    }

    public void clear() {
        start.clear();
        end.clear();
    }

    @Override
    public boolean contains(Locatable l) {
        return l.isGreaterThan(getLow()) && l.isLessThan(getHigh());
    }

    private void setStart(int index, int offset) {
        start.setIndex(index);
        start.setOffset(offset);
    }

    private void setEnd(int index, int offset) {
        end.setIndex(index);
        end.setOffset(offset);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleLocatableRange that = (SimpleLocatableRange) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "LocatableRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

}
