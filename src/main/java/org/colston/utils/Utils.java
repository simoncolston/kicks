/*
 * Created on 2004/02/07
 *
 */
package org.colston.utils;

import org.colston.kicks.KicksMain;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.net.URL;
import java.util.prefs.Preferences;

/**
 * @author simon
 */
public class Utils {
    private static final String LAST_DIR = "open.lastdir";

    public static final String FILE_EXT = ".kicks";
    public static final String PDF_FILE_EXT = ".pdf";

    public static class SingleExtensionFileFilter extends FileFilter {
        private final String extension;
        private final String fileType;

        private SingleExtensionFileFilter(String ext, String fileType) {
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

    public static final FileFilter FILE_FILTER = new SingleExtensionFileFilter(FILE_EXT, "kicks");
    public static final FileFilter PDF_FILE_FILTER = new SingleExtensionFileFilter(PDF_FILE_EXT, "PDF");

    public static Icon createIconFromResource(Class<?> cls, String path) {

        URL url = cls.getResource(path);
        if (url == null) {
            return null;
        }
        return new ImageIcon(url);
    }

    public static File chooseFile(JFrame frame, String title, String approveButtonText, File selectedFile,
                                  FileFilter filter, boolean checkOverwrite, String fileExtension) {
        JFileChooser chooser = new SimpleFileChooser(checkOverwrite, fileExtension);
        if (selectedFile != null) {
            if (selectedFile.isDirectory()) {
                chooser.setCurrentDirectory(selectedFile);
            } else {
                chooser.setSelectedFile(selectedFile);
            }
        } else {
            String dir = getLastDir();
            chooser.setCurrentDirectory(dir == null ? null : new File(dir));
        }
        chooser.setFileFilter(filter);

        chooser.setDialogTitle(title);
        int r = chooser.showDialog(frame, approveButtonText);
        if (r == JFileChooser.APPROVE_OPTION) {
            Preferences.userNodeForPackage(KicksMain.class).put(LAST_DIR, chooser.getCurrentDirectory().toString());
            return chooser.getSelectedFile();
        }
        return null;
    }

    public static File fixFileExtension(File f, String to) {
        String name = f.getName();
        int i = name.lastIndexOf('.');
        if (i < 0) {
            //No extension, so add
            return new File(f.getParentFile(), name + to);
        }
        if (to.equals(name.substring(i))) {
            //Already has correct extension
            return f;
        }
        //remove and replace extension
        return new File(f.getParentFile(), name.substring(0, i) + to);
    }

    public static File getWorkingDirectory() {
        String s = getLastDir();
        if (s == null) {
            s = System.getProperty("user.home");
        }
        return new File(s);
    }

    private static String getLastDir() {
        return Preferences.userNodeForPackage(KicksMain.class).get(LAST_DIR, null);
    }
}
