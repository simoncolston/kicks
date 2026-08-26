package org.colston.kicks.document;

import java.util.Comparator;

public class LocatableComparator implements Comparator<Locatable> {

    public static final Comparator<Locatable> INSTANCE = new LocatableComparator();

    @Override
    public int compare(Locatable o1, Locatable o2) {
        return o1.getIndex() == o2.getIndex() ? o1.getOffset() - o2.getOffset() : o1.getIndex() - o2.getIndex();
    }
}
