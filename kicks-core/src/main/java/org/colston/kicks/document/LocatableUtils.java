package org.colston.kicks.document;

import java.util.List;

public class LocatableUtils {
    public static int calculateTicks(Locatable l) {
        return l.getIndex() * Locatable.CELL_TICKS + l.getOffset();
    }

    public static Locatable findHighest(List<List<? extends Locatable>> locatablesList) {
        Locatable highest = null;
        for (List<? extends Locatable> locatables : locatablesList) {
            highest = findHighest(highest, locatables);
        }
        return highest;
    }

    public static <T extends Locatable> Locatable findLowest(Locatable lowest, List<T> locatables) {
        if (locatables.isEmpty()) {
            return lowest;
        }
        Locatable current = locatables.getFirst();
        return lowest == null || lowest.isGreaterThan(current) ? current : lowest;
    }

    public static <T extends Locatable> Locatable findHighest(Locatable highest, List<T> locatables) {
        if (locatables.isEmpty()) {
            return highest;
        }
        Locatable current = locatables.getLast();
        return highest == null || highest.isLessThan(current) ? current : highest;
    }

}
