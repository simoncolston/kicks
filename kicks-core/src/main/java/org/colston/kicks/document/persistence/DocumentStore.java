package org.colston.kicks.document.persistence;

import org.colston.kicks.document.KicksDocument;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface DocumentStore {
    static DocumentStore create() throws Exception {
        XMLDocumentStore ds = new XMLDocumentStore();
        ds.initialise();
        return ds;
    }

    KicksDocument load(InputStream is) throws Exception;

    KicksDocument load(File is) throws Exception;

    void save(KicksDocument doc, OutputStream os) throws Exception;

    KicksDocument clone(KicksDocument doc);
}
