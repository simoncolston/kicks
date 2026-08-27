package org.colston.lib.gui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SingleExtensionFileFilter extends FileFilter {
    private final String extension;
    private final String fileType;

    public SingleExtensionFileFilter(String ext, String fileType) {
        this.extension = ext;
        this.fileType = fileType;
    }

    @Override
    public String getDescription() {
        return fileType + " files (*" + extension + ")";
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(extension);
    }
}
