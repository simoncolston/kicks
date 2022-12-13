package org.colston.kicks.actions;

import org.colston.gui.actions.ActionManager;
import org.colston.kicks.KicksApp;
import org.colston.lib.i18n.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;

public class About extends AbstractAction {

    public static final String ACTION_COMMAND = "action.about";

    private static final String MESSAGE_RESOURCE_PREFIX = "about";
    private static final String SMALL_ICON_NAME = "About24.png";
    private static final String LARGE_ICON_NAME = "About24.png";

    public About() {
        putValue(ACTION_COMMAND_KEY, ACTION_COMMAND);
        putValue(ActionManager.MESSAGE_RESOURCE_PREFIX_KEY, MESSAGE_RESOURCE_PREFIX);
        putValue(ActionManager.SMALL_ICON_NAME_KEY, SMALL_ICON_NAME);
        putValue(ActionManager.LARGE_ICON_NAME_KEY, LARGE_ICON_NAME);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog dialog = new JDialog(KicksApp.frame(), Messages.get(Title.class, "about.dialog.title"), true);
        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        Image iconImage = KicksApp.getBiggestIconImage();
        JLabel icon = new JLabel(iconImage == null ? null : new ImageIcon(iconImage));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel(KicksApp.APPLICATION_NAME);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel version = new JLabel("Version unknown");
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        // TODO: Move this manifest work to somewhere more generic
        URL url = getClass().getResource("/META-INF/MANIFEST.MF");
        if (url != null) {
            try (InputStream is = url.openStream()) {
                Manifest manifest = new Manifest(is);
                appName.setText(manifest.getMainAttributes().getValue("Implementation-Title"));
                version.setText(manifest.getMainAttributes().getValue("Implementation-Version"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        JLabel description = new JLabel(Messages.get(Title.class, "about.dialog.app.description"));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel copyright = new JLabel("© 2009 Simon Colston");
        copyright.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(icon);
        content.add(Box.createVerticalStrut(10));
        content.add(appName);
        content.add(Box.createVerticalStrut(10));
        content.add(version);
        content.add(Box.createVerticalStrut(10));
        content.add(description);
        content.add(Box.createVerticalStrut(10));
        content.add(copyright);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(KicksApp.frame());
        dialog.setResizable(false);

        dialog.setVisible(true);
    }
}
