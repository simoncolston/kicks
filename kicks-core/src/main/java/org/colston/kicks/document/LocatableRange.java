package org.colston.kicks.document;

public interface LocatableRange {
    boolean isEmpty();

    Locatable getLow();

    Locatable getHigh();

    boolean contains(Locatable l);
}
