package org.colston.utils;

import org.colston.lib.i18n.Messages;

import javax.swing.*;

public class SimpleFileChooser extends JFileChooser {
    private final boolean checkOverwrite;
    private final String fileExtension;

    public SimpleFileChooser(boolean checkOverwrite, String fileExtension) {
        this.checkOverwrite = checkOverwrite;
        this.fileExtension = fileExtension;
    }

    @Override
    public void approveSelection() {
        if (!checkOverwrite) {
            super.approveSelection();
            return;
        }

        if (fileExtension != null) {
            setSelectedFile(Utils.fixFileExtension(getSelectedFile(), fileExtension));
        }
        if (!getSelectedFile().exists()) {
            super.approveSelection();
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, Messages.get(SimpleFileChooser.class,
                "simple.file.chooser.message"), Messages.get(SimpleFileChooser.class, "simple.file.chooser.title"),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        switch (result) {
            case JOptionPane.YES_OPTION:
                super.approveSelection();
                break;
            case JOptionPane.NO_OPTION:
                //Do nothing - returns to file chooser
                break;
            case JOptionPane.CANCEL_OPTION:
                cancelSelection();
                break;
        }
    }
}
