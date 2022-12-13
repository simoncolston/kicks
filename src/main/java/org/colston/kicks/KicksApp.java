package org.colston.kicks;

import org.colston.gui.actions.ActionManager;
import org.colston.gui.actions.ActionProvider;
import org.colston.gui.actions.ActionProviders;
import org.colston.kicks.actions.*;
import org.colston.kicks.document.KicksDocument;
import org.colston.kicks.document.persistence.DocumentStore;
import org.colston.kicks.gui.canvas.Canvas;
import org.colston.kicks.gui.canvas.CanvasFactory;
import org.colston.lib.gui.GuiApp;
import org.colston.lib.gui.task.Task;
import org.colston.lib.i18n.Messages;
import org.colston.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KicksApp extends GuiApp {

    public static final String APPLICATION_NAME = "kicks";
    public static final Color PANEL_COLOUR = new Color(53, 119, 171);
    private static final Logger logger = Logger.getLogger(KicksApp.class.getName());
    private static final String[] ICON_RESOURCES = {
            "icon96.png",
            "icon64.png",
            "icon48.png",
            "icon32.png",
            "icon24.png",
            "icon16.png",
    };
    private static final String SPLASH_FILE_NAME = "splash.png";

    public static final String FONT_NAME = "HanaMinA Regular";
    public static final String FONT_RESOURCE_NAME = "HanaMinA.ttf";
//    public static final String FONT_NAME = "Harano Aji Mincho";
//    public static final String FONT_RESOURCE_NAME = "HaranoAjiMincho-Regular.otf";

    private static KicksApp kicks;
    //TODO: Make this system dependent?  Go for a "dot file" config?
    private final Settings settings = new PreferencesSettings();

    private Canvas canvas;
    private final MainActionProvider mainActionProvider = new MainActionProvider();
    private DocumentStore documentStore;
    private File currentFile = null;

    public static void main(String[] args) {
        kicks = new KicksApp();
        kicks.start(args);
    }

    public static Settings settings() {
        return kicks.settings;
    }

    public static DocumentStore documentStore() {
        return kicks.documentStore;
    }

    public static JFrame frame() {
        return kicks.frame;
    }

    public static Canvas canvas() {
        return kicks.canvas;
    }

    public static File getCurrentFile() {
        return kicks.currentFile;
    }

    public static void documentSaved(File file) {
        canvas().documentSaved();
        setCurrentFile(file);
    }

    private static void setCurrentFile(File currentFile) {
        kicks.currentFile = currentFile;
        kicks.statusPanel.setMessage(currentFile == null ? " " : currentFile.getAbsolutePath());
    }

    @Override
    protected Icon getSplashIcon() {
        return Utils.createIconFromResource(KicksApp.class, SPLASH_FILE_NAME);
    }

    @Override
    protected String getConfigDirName() {
        return "." + APPLICATION_NAME;
    }

    @Override
    protected String getApplicationName() {
        return APPLICATION_NAME;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void configureOther(String[] args) throws Exception {

         // XML Schema load
        documentStore = DocumentStore.create();

         // Load fonts
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try (BufferedInputStream bis = new BufferedInputStream(openFontResourceInputStream())) {
            Font f = Font.createFont(Font.TRUETYPE_FONT, bis);
            graphics.registerFont(f);
        }
    }

    public static InputStream openFontResourceInputStream() {
        return KicksApp.class.getResourceAsStream(FONT_RESOURCE_NAME);
    }

    @Override
    protected List<? extends Image> getFrameIconImages() {
        try {
            List<Image> images = new ArrayList<>(ICON_RESOURCES.length);
            for (String iconResource : ICON_RESOURCES) {
                images.add(ImageIO.read(Objects.requireNonNull(KicksApp.class.getResource(iconResource))));
            }
            return images;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading frame icon images", e);
        }
        return null;
    }

    public static Image getBiggestIconImage() {
        try {
            return ImageIO.read(Objects.requireNonNull(KicksApp.class.getResource(ICON_RESOURCES[0])));
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error loading biggest icon image", e);
        }
        return null;
    }

    @Override
    protected WindowListener getFrameWindowListener() {
        return new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (checkSaveChangesToCurrentDocument()) {
                    frame.dispose();
                }
            }
        };
    }

    public static boolean checkSaveChangesToCurrentDocument() {
        if (!canvas().isDocumentChanged()) {
            return true;
        }
        String message = Messages.get(KicksApp.class, "save.changes.message");
        String title = Messages.get(KicksApp.class, "save.changes.title");
        Object[] options = new Object[]
                {
                        Messages.get(KicksApp.class, "save.changes.save"),
                        Messages.get(KicksApp.class, "save.changes.dont.save"),
                        Messages.get(KicksApp.class, "save.changes.cancel")
                };
        int ret = JOptionPane.showOptionDialog(frame(), message, title, JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        boolean complete = false;
        switch (ret) {
            case 0: // save then exit (unless user cancels during save)
                if (ActionManager.getAction(Save.class).save()) {
                    complete = true;
                }
                break;
            case 1: // close without saving
                complete = true;
                break;
            case JOptionPane.CLOSED_OPTION: // dialog was closed
            case 2: // cancel
            default:
                // Don't exit
                break;
        }
        return complete;
    }

    @Override
    protected JComponent createMainPanel() {
        // All tasks use this root pane by default
        Task.getConfig().setRootPane(frame);

        //main actions
        ActionProviders.register(mainActionProvider);

        //create other components that can register action providers
        canvas = CanvasFactory.create();
        ActionProviders.register(canvas.getActionProvider());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.GRAY);

        JScrollPane scroller = new JScrollPane(canvas.getContainer());
        mainPanel.add(scroller, BorderLayout.CENTER);
        mainPanel.add(canvas.getInputComponent(), BorderLayout.SOUTH);

        statusPanel.setMessage(APPLICATION_NAME);

        return mainPanel;
    }

    @Override
    protected JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        boolean added = addButtons(toolBar, "menu.file", false);
        added = addButtons(toolBar, "menu.edit", added);
        addButtons(toolBar, "menu.document", added);
        addButtons(toolBar, "menu.view", added);
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

    @Override
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        addMenu(menuBar, "menu.file");
        addMenu(menuBar, "menu.edit");
        addMenu(menuBar, "menu.document");
        addMenu(menuBar, "menu.help");
        return menuBar;
    }

    private static void addMenu(JMenuBar menuBar, String menuName) {
        JMenu menu = new JMenu();
        menuBar.add(menu);
        menu.setText(Messages.get(KicksApp.class, menuName));
        String s = Messages.get(KicksApp.class, menuName + ".mnemonic");
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

    @Override
    protected void requestFocusInWindow() {
        canvas.requestFocusInWindow();
    }

    @Override
    protected void loadApplicationData(String[] args) {
        loadDocument(args);
    }

    private void loadDocument(String[] args) {
        if (args.length < 1) {
            return;
        }
        File file = new File(args[0]);
        if (!file.exists() || !file.canRead()) {
            getLogger().log(Level.SEVERE, "Cannot read file: {1}", file.getAbsolutePath());
            return;
        }
        try {
            openDocumentFromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDocumentFromFile(File file) throws Exception {
        setDocument(file, documentStore().load(file));
    }

    public static void setDocument(File file, KicksDocument document) {
        canvas().setDocument(document);
        setCurrentFile(file);
    }

    public static void newDocument() {
        setDocument(null, new KicksDocument());
    }

    private static class MainActionProvider implements ActionProvider {

        private final List<Action> fileActions = new ArrayList<>();
        private final List<Action> viewActions = new ArrayList<>();
        private final List<Action> helpActions = new ArrayList<>();

        public MainActionProvider() {
            fileActions.add(ActionManager.getAction(New.class));
            fileActions.add(ActionManager.getAction(Open.class));
            fileActions.add(ActionManager.getAction(Save.class));
            fileActions.add(ActionManager.getAction(SaveAs.class));
            fileActions.add(ActionManager.getAction(ExportAsPDF.class));
            fileActions.add(ActionManager.getAction(Print.class));
            fileActions.add(ActionManager.getAction(SettingsAction.class));
            fileActions.add(ActionManager.getAction(Quit.class));

            viewActions.add(ActionManager.getAction(ZoomOut.class));
            viewActions.add(ActionManager.getAction(ZoomReset.class));
            viewActions.add(ActionManager.getAction(ZoomIn.class));

            helpActions.add(ActionManager.getAction(KeyboardShortcuts.class));
            helpActions.add(ActionManager.getAction(About.class));
        }

        @Override
        public List<Action> getMenuActions(String menuName) {
            return switch (menuName) {
                case "menu.file" -> fileActions;
                case "menu.help" -> helpActions;
                default -> null;
            };
        }

        @Override
        public List<Action> getToolBarActions(String menuName) {
            List<Action> list = new ArrayList<>();
            switch (menuName) {
                case "menu.file" -> {
                    list.add(ActionManager.getAction(New.class));
                    list.add(ActionManager.getAction(Open.class));
                    list.add(ActionManager.getAction(Save.class));
                    list.add(ActionManager.getAction(SaveAs.class));
                    list.add(ActionManager.getAction(ExportAsPDF.class));
                    list.add(ActionManager.getAction(Print.class));
                }
                case "menu.view" -> {
                    list.add(ActionManager.getAction(ZoomOut.class));
                    list.add(ActionManager.getAction(ZoomReset.class));
                    list.add(ActionManager.getAction(ZoomIn.class));
                }
            }
            return list;
        }

        @Override
        public Collection<? extends Action> getAllActions() {
            List<Action> actions = new ArrayList<>(fileActions);
            actions.addAll(viewActions);
            actions.addAll(helpActions);
            return actions;
        }
    }
}
