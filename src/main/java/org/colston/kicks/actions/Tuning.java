package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksMain;
import org.colston.kicks.document.KicksDocument;
import org.colston.sclib.i18n.Messages;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Tuning extends AbstractAction {
    public static final String ACTION_COMMAND = "action.tuning";

    private static final String MESSAGE_RESOURCE_PREFIX = "tuning";

    public Tuning() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        KicksDocument doc = KicksMain.getCanvas().getDocument();
        Object[] objs = org.colston.kicks.document.Tuning.values();
        org.colston.kicks.document.Tuning t = (org.colston.kicks.document.Tuning) JOptionPane.showInputDialog(KicksMain.getFrame(),
                Messages.get(Tuning.class, "tuning.dialog.message"), Messages.get(Tuning.class, "tuning.dialog.title"),
                JOptionPane.PLAIN_MESSAGE, null, objs, doc.getTuning());
        if (t != null) {
            doc.setTuning(t);
        }
    }
}
