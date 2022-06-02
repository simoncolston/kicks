package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ActionProvider;
import org.colston.gui.actions.ActionProviders;
import org.colston.kicks.KicksApp;
import org.colston.sclib.i18n.Messages;
import org.colston.utils.Utils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class KeyboardShortcuts extends AbstractAction {

    public static final String ACTION_COMMAND = "action.keyboard.shortcuts";
    private static final String MESSAGE_RESOURCE_PREFIX = "keyboard.shortcuts";

    public KeyboardShortcuts() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JDialog dialog = new JDialog(KicksApp.frame(),
                Messages.get(Title.class, "keyboard.shortcuts.dialog.title"), true);
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Action> actions = new ArrayList<>();
        for (ActionProvider ap : ActionProviders.getAllProviders()) {
            actions.addAll(ap.getAllActions().stream()
                    .filter(action -> action.getValue(Action.ACCELERATOR_KEY) != null).toList());
        }

        JTable table = new JTable(new ActionTableModel(actions));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);
        table.getColumnModel().getColumn(0).setMinWidth(100);
        table.getColumnModel().getColumn(1).setMinWidth(100);
        table.getColumnModel().getColumn(2).setMinWidth(100);
        Dimension size = table.getPreferredSize();
        // only show 12 rows maximum
        if (size.height > table.getRowHeight() * 12) {
            size.height = table.getRowHeight() * 12;
        }
        table.setPreferredScrollableViewportSize(size);
        JScrollPane scrollPane = new JScrollPane(table);
        content.add(scrollPane, BorderLayout.CENTER);

        dialog.setContentPane(content);
        dialog.pack();

        dialog.setLocationRelativeTo(KicksApp.frame());
        dialog.setVisible(true);
    }

    private static class ActionTableModel extends AbstractTableModel {

        private final List<Action> actions;

        private ActionTableModel(List<Action> actions) {
            this.actions = actions;
        }

        @Override
        public int getRowCount() {
            return actions.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Action a = actions.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> Utils.toString((KeyStroke) a.getValue(Action.ACCELERATOR_KEY));
                case 1 -> a.getValue(Action.NAME);
                case 2 -> a.getValue(Action.SHORT_DESCRIPTION);
                default ->
                    throw new IllegalStateException("Unexpected value: " + columnIndex);
            };
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case 0 -> Messages.get(KeyboardShortcuts.class, "keyboard.shortcuts.table.key");
                case 1 -> Messages.get(KeyboardShortcuts.class, "keyboard.shortcuts.table.name");
                case 2 -> Messages.get(KeyboardShortcuts.class, "keyboard.shortcuts.table.description");
                default -> throw new IllegalStateException("Unexpected value: " + column);
            };
        }
    }
}
