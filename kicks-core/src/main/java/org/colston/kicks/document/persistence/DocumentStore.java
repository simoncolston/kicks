package org.colston.kicks.document.persistence;

import org.colston.kicks.document.KicksDocument;

import java.io.*;

public interface DocumentStore {
    KicksDocument load(InputStream is) throws Exception;

    KicksDocument load(File file) throws Exception;

    default void save(KicksDocument doc, File file) throws Exception  {
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
            save(doc, os);
        }
    }

    void save(KicksDocument doc, OutputStream os) throws Exception;

    default KicksDocument clone(KicksDocument doc) {
        throw new UnsupportedOperationException();
    }
}
