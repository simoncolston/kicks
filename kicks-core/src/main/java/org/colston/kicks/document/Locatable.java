package org.colston.kicks.document;

public interface Locatable {
    int CELL_TICKS = 12;

    int getIndex();

    int getOffset();

    default void move(int indexDelta, int offsetDelta) {
        throw new UnsupportedOperationException();
    }

    default int getTicks() {
        return LocatableUtils.calculateTicks(this);
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
        return "(index: " + getIndex() + ", offset: " + getOffset() + ")";
    }
}
