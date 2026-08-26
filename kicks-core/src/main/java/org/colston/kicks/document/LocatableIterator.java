package org.colston.kicks.document;

import java.util.Iterator;
import java.util.List;

class LocatableIterator<T extends Locatable> implements Iterator<T> {

    private final List<T> list;
    private final LocatableRange range;
    private int i = 0;
    private T current;

    public LocatableIterator(List<T> list, LocatableRange range) {
        this.list = list;
        this.range = range;
    }

    @Override
    public boolean hasNext() {
        while (i < list.size() && current == null) {
            current = list.get(i++);
            if (!range.contains(current)) {
                current = null;
            }
        }
        return current != null;
    }

    @Override
    public T next() {
        T n = current;
        current = null;
        return n;
    }
}
