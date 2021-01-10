package org.colston.kicks;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ActionProvider;
import org.colston.gui.actions.ActionProviders;
import org.colston.gui.task.TaskListener;
import org.colston.gui.task.TaskListeners;
import org.colston.kicks.actions.*;
import org.colston.kicks.document.persistence.DocumentStore;
import org.colston.kicks.gui.canvas.Canvas;
import org.colston.kicks.gui.canvas.CanvasFactory;
import org.colston.sclib.gui.Splash;
import org.colston.sclib.gui.StatusPanel;
import org.colston.sclib.i18n.Messages;
import org.colston.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main startup class for the application.
 */
public class KicksMain {
    private static final String splashFileName = "splash.png";

    public static final String APPLICATION_NAME = "kicks";

    public static final String FONT_NAME = "HanaMinA Regular";
    public static final String FONT_RESOURCE_NAME = "HanaMinA.ttf";

    private static final String[] ICON_RESOURCES =
            {
                    "icon16.png",
                    "icon24.png",
                    "icon32.png",
                    "icon48.png",
                    "icon64.png",
                    "icon96.png",
            };

    private static JFrame frame;
    private static Canvas canvas;
    private static StatusPanel statusPanel;

    //TODO: Make this system dependent?  Go for a "dot file" config?
    private static final Settings settings = new PreferencesSettings();
    private static DocumentStore documentStore;
    private static final MainActionProvider mainActionProvider = new MainActionProvider();

    private static File currentFile = null;

    public static JFrame getFrame() {
        return frame;
    }

    public static Canvas getCanvas() {
        return canvas;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static DocumentStore getDocumentStore() {
        return documentStore;
    }

    public static File getCurrentFile() {
        return currentFile;
    }

    public static void setCurrentFile(File f) {

        currentFile = f;
        statusPanel.setMessage(currentFile == null ? "" : currentFile.getAbsolutePath());
    }

    /**
     * Performs all global configuration.
     */
    private static void configure() {
        try {
            /*
             * XML Schema load
             */
            documentStore = DocumentStore.create();

            /*
             * Choose a look and feel
             */
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            /*
             * Load fonts
             */
            GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try (BufferedInputStream bis = new BufferedInputStream(openFontResourceInputStream())) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, bis);
                graphics.registerFont(f);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static InputStream openFontResourceInputStream() {
        return KicksMain.class.getResourceAsStream(FONT_RESOURCE_NAME);
    }

    /**
     * Main method.
     *
     * @param args arguments
     */
    public static void main(String[] args) {

        // create the frame for the splash to cover
        frame = new JFrame(APPLICATION_NAME);

        // create and show the splash screen
        final Splash splash = new Splash(frame, Utils.createIconFromResource(KicksMain.class, splashFileName));
        splash.showSplash();

        //give ourselves minimum time for splash screen
        long startMillis = System.currentTimeMillis();

        KicksMain.configure();

        createUI();

        // show the GUI and get rid of the splash
        EventQueue.invokeLater(() ->
        {
            frame.pack();
            frame.setLocationRelativeTo(null);

            long timeLeft = 3000 - (System.currentTimeMillis() - startMillis);
            if (timeLeft > 0) {
                //pause
                try {
                    Thread.sleep(timeLeft);
                } catch (InterruptedException ignored) {
                }
            }
            // end pause
            frame.setVisible(true);
            canvas.requestFocusInWindow();
            splash.disposeSplash();
        });
    }

    private static void createUI() {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (!canvas.isDocumentChanged()) {
                    frame.dispose();
                    return;
                }
                String message = Messages.get(KicksMain.class, "window.closing.message");
                String title = Messages.get(KicksMain.class, "window.closing.title");
                Object[] options = new Object[]
                        {
                                Messages.get(KicksMain.class, "window.closing.save"),
                                Messages.get(KicksMain.class, "window.closing.dont.save"),
                                Messages.get(KicksMain.class, "window.closing.cancel")
                        };
                int ret = JOptionPane.showOptionDialog(frame, message, title, JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                switch (ret) {
                    case 0:
                        if (ActionManager.getAction(Save.class).save()) {
                            frame.dispose();
                        }
                        break;

                    case 1:
                        frame.dispose();
                        break;

                    case JOptionPane.CLOSED_OPTION:
                    case 2:
                    default:
                        // Don't exit
                        break;
                }
            }
        });

        try {
            List<Image> images = new ArrayList<>(ICON_RESOURCES.length);
            for (String iconResource : ICON_RESOURCES) {
                images.add(ImageIO.read(KicksMain.class.getResource(iconResource)));
            }
            frame.setIconImages(images);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //main actions
        ActionProviders.register(mainActionProvider);
        TaskListeners.register(mainActionProvider);

        //create other components that can register action providers
        canvas = CanvasFactory.create();
        ActionProviders.register(canvas.getActionProvider());
        TaskListeners.register(canvas.getTaskListener());

        statusPanel = new StatusPanel();

        frame.setTitle(APPLICATION_NAME);
        frame.setJMenuBar(createMenuBar());
        JPanel contentPane = new JPanel(new BorderLayout());
        frame.setContentPane(contentPane);
        contentPane.add(createToolBar(), BorderLayout.NORTH);

        JPanel mp = new JPanel(new BorderLayout());
        mp.setBackground(Color.GRAY);

        JScrollPane scroller = new JScrollPane(canvas.getContainer());
        mp.add(scroller, BorderLayout.CENTER);

        mp.add(canvas.getInputComponent(), BorderLayout.SOUTH);

        contentPane.add(mp, BorderLayout.CENTER);

        contentPane.add(statusPanel, BorderLayout.SOUTH);

        statusPanel.setMessage(APPLICATION_NAME);

    }

    private static JToolBar createToolBar() {

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        boolean added = addButtons(toolBar, "menu.file", false);
        added = addButtons(toolBar, "menu.edit", added);
        addButtons(toolBar, "menu.document", added);
        return toolBar;
    }

    private static boolean addButtons(JToolBar toolBar, String menuName, boolean added) {
        boolean flag = added;
        for (ActionProvider ap : ActionProviders.getAllProviders()) {
            List<Action> actions = ap.getToolBarActions(menuName);
            if (actions == null || actions.isEmpty()) {
                continue;
            }
            if (flag) {
                toolBar.addSeparator();
            }
            flag = true;
            for (Action a : actions) {
                add(toolBar, a);
            }
        }
        return flag;
    }

    private static void add(JToolBar toolBar, Action action) {
        JButton b = new JButton();
        b.setFocusable(false);
        toolBar.add(b);
        b.setAction(action);
        if (action.getValue(Action.LARGE_ICON_KEY) != null) {
            b.setHideActionText(true);
        }
    }

    /**
     * Create the menu bar
     *
     * @return menu bar
     */
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        addMenu(menuBar, "menu.file");
        addMenu(menuBar, "menu.edit");
        addMenu(menuBar, "menu.document");
        return menuBar;
    }

    private static void addMenu(JMenuBar menuBar, String menuName) {
        JMenu menu = new JMenu();
        menuBar.add(menu);
        menu.setText(Messages.get(KicksMain.class, menuName));
        String s = Messages.get(KicksMain.class, menuName + ".mnemonic");
        menu.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(s.codePointAt(0)));
        populateMenu(menu, menuName);
    }

    private static void populateMenu(JMenu menu, String menuName) {
        boolean added = false;
        for (ActionProvider ap : ActionProviders.getAllProviders()) {
            List<Action> actions = ap.getMenuActions(menuName);
            if (actions == null || actions.isEmpty()) {
                continue;
            }
            //put a separator between providers
            if (added) {
                menu.addSeparator();
            }
            added = true;
            for (Action a : actions) {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(a);
                menu.add(menuItem);
            }
        }
    }

    private static class MainActionProvider implements ActionProvider, TaskListener {
        private static final List<Action> actions = new ArrayList<>();

        static {
            actions.add(ActionManager.getAction(Open.class));
            actions.add(ActionManager.getAction(Save.class));
            actions.add(ActionManager.getAction(SaveAsPDF.class));
            actions.add(ActionManager.getAction(Print.class));
            actions.add(ActionManager.getAction(SettingsAction.class));
            actions.add(ActionManager.getAction(Quit.class));
        }

        @Override
        public List<Action> getMenuActions(String menuName) {
            if ("menu.file".equals(menuName)) {
                return Collections.unmodifiableList(actions);
            }
            return null;
        }

        @Override
        public List<Action> getToolBarActions(String menuName) {
            List<Action> list = new ArrayList<>();
            if ("menu.file".equals(menuName)) {
                list.add(ActionManager.getAction(Open.class));
                list.add(ActionManager.getAction(Save.class));
                list.add(ActionManager.getAction(SaveAsPDF.class));
                list.add(ActionManager.getAction(Print.class));
            }
            return list;
        }

        @Override
        public void taskStarted() {
            for (Action a : actions) {
                a.setEnabled(false);
            }
        }

        @Override
        public void taskEnded() {
            for (Action a : actions) {
                a.setEnabled(true);
            }
        }
    }
}
