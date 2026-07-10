package org.colston.kicks.document;

public interface Locatable {
    int CELL_TICKS = 12;

    int getIndex();

    int getOffset();

    default void move(int indexDelta, int offsetDelta) {
        throw new UnsupportedOperationException();
    }

    default int getTicks() {
        return calculateTicks(getIndex(), getOffset());
    }

    static int calculateTicks(int index, int offset) {
        return index * Locatable.CELL_TICKS + offset;
    }

    default boolean isLessThan(Locatable l) {
        return getTicks() <= l.getTicks();
    }

    default boolean isGreaterThan(Locatable l) {
        return getTicks() >= l.getTicks();
    }

    default boolean isEqualTo(Locatable l) {
        return getIndex() == l.getIndex() && getOffset() == l.getOffset();
    }

    default String locationAsString() {
        return "Locatable (index: " + getIndex() + ", offset: " + getOffset() + ")";
    }
}
