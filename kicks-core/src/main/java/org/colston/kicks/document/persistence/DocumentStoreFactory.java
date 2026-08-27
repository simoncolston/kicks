package org.colston.kicks.document.persistence;

import java.io.File;
import java.util.Optional;

public class DocumentStoreFactory {

    static String KICKS_FILE_EXT = ".kicks";
    static String KICKSABC_FILE_EXT = ".kicksabc";

    public static Optional<DocumentStore> create(File file) throws Exception {
        if (file.getName().endsWith(KICKS_FILE_EXT)) {
            return Optional.of(createDefault());
        } else if (file.getName().endsWith(KICKSABC_FILE_EXT)) {
            return Optional.of(new KicksABCDocumentStore());
        }
        return Optional.empty();
    }

    public static DocumentStore createDefault() throws Exception {
        XMLDocumentStore ds = new XMLDocumentStore();
        ds.initialise();
        return ds;
    }
}
