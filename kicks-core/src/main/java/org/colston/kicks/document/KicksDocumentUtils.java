package org.colston.kicks.document;

public class KicksDocumentUtils {

    public static int calculateHighestIndex(KicksDocument doc) {
        int highestIndex = doc.getSongs().isEmpty() ? 0 : doc.getSongs().getLast().getIndex();
        Locatable highest = LocatableUtils.findHighest(doc.getAllLocatables());
        return highest != null ?  Math.max(highest.getIndex(), highestIndex) : highestIndex;
    }
}
