package org.colston.kicks.document;

import java.util.EventListener;

public interface KicksDocumentListener extends EventListener {
    public void documentUpdated();

    public void locationUpdated(int index, int offset);
}
