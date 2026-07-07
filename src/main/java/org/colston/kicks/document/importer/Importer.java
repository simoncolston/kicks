package org.colston.kicks.document.importer;

import org.colston.kicks.document.KicksDocument;

import java.io.File;

public interface Importer {

    String KICKSABC_FILE_EXT = ".kicksabc";

    KicksDocument importFile(File file) throws Exception;
}
