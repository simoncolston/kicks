package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.document.KicksDocument;
import org.colston.sclib.i18n.Messages;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Title extends AbstractAction {
    public static final String ACTION_COMMAND = "action.title";

    private static final String MESSAGE_RESOURCE_PREFIX = "title";

    public Title() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        KicksDocument doc = KicksApp.canvas().getDocument();
        String s = (String) JOptionPane.showInputDialog(KicksApp.frame(),
                Messages.get(Title.class, "title.dialog.message"), Messages.get(Title.class, "title.dialog.title"),
                JOptionPane.PLAIN_MESSAGE, null, null, doc.getTitle());
        if (s != null) {
            doc.setTitle(s);
        }
    }
}
