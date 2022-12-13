package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.kicks.Settings;
import org.colston.lib.i18n.Messages;
import org.colston.utils.SpringUtilities;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SettingsAction extends AbstractAction {
    public static final String ACTION_COMMAND = "action.settings";

    private static final String MESSAGE_RESOURCE_PREFIX = "settings";

    private JDialog dialog;
    private JComboBox<Character.Subset[]> charSubsetCombo;
    private final Character.Subset[][] charSubsets = new Character.Subset[][]{Settings.HIRAGANA, Settings.KATAKANA};

    public SettingsAction() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
    }

    private JDialog getDialog() {

        if (dialog == null) {

            dialog = new JDialog(KicksApp.frame(), Messages.get(SettingsAction.class, "settings.dialog.title"), true);

            JPanel panel = new JPanel(new SpringLayout());
            dialog.add(panel, BorderLayout.CENTER);

            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(new JLabel(Messages.get(SettingsAction.class, "settings.default.ime.charset.prompt")));

            charSubsetCombo = new JComboBox<>(charSubsets);
            panel.add(charSubsetCombo, BorderLayout.NORTH);

            charSubsetCombo.setRenderer(new CharacterSubsetRenderer());

            SpringUtilities.makeCompactGrid(panel, 1, 2, 5, 5, 5, 5);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            dialog.add(buttons, BorderLayout.SOUTH);

            buttons.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            JButton b = new JButton(Messages.get(SettingsAction.class, "settings.submit.button"));
            buttons.add(b);

            b.addActionListener(e -> {
                KicksApp.settings().setCharacterSubset((Character.Subset[]) charSubsetCombo.getSelectedItem());
                dialog.setVisible(false);
                dialog.dispose();
            });

            dialog.getRootPane().setDefaultButton(b);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.pack();
            dialog.setLocationRelativeTo(KicksApp.frame());
        }
        charSubsetCombo.requestFocusInWindow();
        return dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog d = getDialog();
        charSubsetCombo.setSelectedItem(KicksApp.settings().getCharacterSubset());
        d.setVisible(true);
    }

    private static class CharacterSubsetRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {

            if (!(value instanceof Character.Subset[] v)) {
                return null;
            }
            String s = Messages.get(SettingsAction.class, "settings.ime." + v[0]);
            return super.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus);
        }
    }
}
