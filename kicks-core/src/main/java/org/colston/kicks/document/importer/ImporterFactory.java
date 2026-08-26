package org.colston.kicks.document.importer;

import java.io.File;
import java.util.Optional;

public class ImporterFactory {
    public static Optional<Importer> getImporter(File file) {
        if (file.getName().endsWith(Importer.KICKSABC_FILE_EXT)) {
            return Optional.of(new KicksABCImporter());
        }
        return Optional.empty();
    }
}
