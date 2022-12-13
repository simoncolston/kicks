package org.colston.lib.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Usage:  Extend, implement all the methods then create a main method like this:
 * <code>
 *     public static void main(String[] args) {
 * 		podge = new PodgeMain();
 * 		podge.start();
 *    }
 * </code>
 */
public abstract class GuiApp {

    protected JFrame frame;
    protected StatusPanel statusPanel;
    protected Path configDir;

    public void start(String[] args) {

        // create the frame for the splash to cover
        frame = new JFrame(getApplicationName());

        // create and show the splash screen
        Icon icon = getSplashIcon();
        if (icon == null) {
        	icon = new SplashIconDefault();
        }
        final Splash splash = new Splash(frame, icon);
        splash.showSplash();

        //give ourselves minimum time for splash screen
        long startMillis = System.currentTimeMillis();
        try {
            configure(args);
            createUI(args);
            loadApplicationData(args);
        } catch (Throwable e) {
            splash.disposeSplash();
            getLogger().log(Level.SEVERE, "Error initialising app", e);
            System.exit(1);
        }

        // show the GUI and get rid of the splash
        EventQueue.invokeLater(() -> {
            frame.pack();
            long timeLeft = 3000 - (System.currentTimeMillis() - startMillis);
            if (timeLeft > 0) {
                //pause
                try {
                    Thread.sleep(timeLeft);
                } catch (InterruptedException ignored) {
                }
            }
            // end pause
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            requestFocusInWindow();
            splash.disposeSplash();
        });
    }

    /**
     * Get the icon to display as a splash screen.
     * @return splash screen icon
     */
    protected abstract Icon getSplashIcon();

    protected void configure(String[] args) throws Exception {
        configureConfigDir(args);
        configureLogging(args);
        configureLookAndFeel(args);
        configureOther(args);
    }

    protected void configureConfigDir(String[] args) throws IOException {
        configDir = Path.of(System.getProperty("user.home"), getConfigDirName());
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            // no logging available yet so output something
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * The name of the directory that will be created in the user's home directory to store configuration, logs, etc.
     * e.g. {@code ".guiapp"} (remember to put the dot in!) TODO: Make this work on Windows!!!
     * @return config directory name
     */
    protected abstract String getConfigDirName();

    public Path getConfigDir() {
        return configDir;
    }

    /**
     * Do any application specific configuration tasks e.g. load XML schema, load fonts, etc.
     * @throws Exception error during configuration
     * @param args parameter arguments
     */
    protected abstract void configureOther(String[] args) throws Exception;

    /**
     * Configures logging.  Looks for {@code logging.properties} in {@link #getConfigDirName()} and uses that.
     * If it does not exist then one is generated with some sensible defaults.
     * @throws Exception error reading a writing properties
     * @param args program arguments
     */
    protected void configureLogging(String[] args) throws Exception {
        try {
            Path loggingPropertiesFile = getConfigDir().resolve("logging.properties");
            if (Files.notExists(loggingPropertiesFile)) {
                Properties ps = new Properties();
                ps.setProperty("handlers", "java.util.logging.FileHandler, java.util.logging.ConsoleHandler");
                ps.setProperty(".level", "INFO");
                ps.setProperty("java.util.logging.ConsoleHandler.level", "ALL");
                ps.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
                ps.setProperty("java.util.logging.FileHandler.level", "ALL");
                ps.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
                ps.setProperty("java.util.logging.FileHandler.limit", "0");
                ps.setProperty("java.util.logging.FileHandler.count", "1");
                ps.setProperty("java.util.logging.FileHandler.pattern", "%h/" + getConfigDirName() + "/log/app.log");
                ps.setProperty("java.util.logging.FileHandler.append", "true");

                try (BufferedWriter w = Files.newBufferedWriter(loggingPropertiesFile)) {
                    ps.store(w, "Generated by " + getApplicationName());
                }

                //create directory for log files
                Files.createDirectories(getConfigDir().resolve("log"));
            }
            System.setProperty("java.util.logging.config.file", loggingPropertiesFile.toAbsolutePath().toString());
            LogManager.getLogManager().readConfiguration();
        } catch (SecurityException | IOException e) {
            // no logging available so print something
            e.printStackTrace();
            throw e;
        }
    }

    protected void configureLookAndFeel(String[] args) throws Exception {
//        String laf = null;
//        final String GTK_LAF = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
//        for (LookAndFeelInfo l : UIManager.getInstalledLookAndFeels()) {
//            if (GTK_LAF.equals(l.getClassName())) {
//                laf = l.getClassName();
//                break;
//            }
//        }
        String laf = UIManager.getCrossPlatformLookAndFeelClassName();
        getLogger().log(Level.INFO, "Using: {0}", laf);
        UIManager.setLookAndFeel(laf);
    }

    /**
     * Create the UI.  The default implementation creates a frame with a menu bar.  The main panel has a border layout
     * with a toolbar in the north and status panel in the south.  Abstract methods are used to create the missing
     * pieces.
     * @param args programme arguments
     */
    protected void createUI(String[] args) {
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(getFrameWindowListener());
        frame.setIconImages(getFrameIconImages());

        statusPanel = new StatusPanel();

        JComponent mainPanel = createMainPanel();
        JMenuBar menuBar = createMenuBar();
        JToolBar toolBar = createToolBar();

        frame.setJMenuBar(menuBar);
        JPanel contentPane = new JPanel(new BorderLayout());
        frame.setContentPane(contentPane);
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setMessage(getApplicationName());
    }

    /**
     * Provides a simple name for the application.  Used in window title bar, etc.
     * @return application name
     */
    protected abstract String getApplicationName();

    /**
     * Get a logger to use during app initialisation for information and errors.
     * @return a logger
     */
    protected abstract Logger getLogger();

    /**
     * Get a list of images to use as icons for the frame in the OS windowing system.  An image of the appropriate
     * size for the use-case will be selected by swing.
     * @return list of icon images of different sizes
     */
    protected abstract List<? extends Image> getFrameIconImages();

    /**
     * Get the window listener used by the frame.  This can be used to check if a document needs saving before closing,
     * etc.
     * @return frame window listener
     */
    protected abstract WindowListener getFrameWindowListener();

    /**
     * Create the main panel that goes in the centre of the the content pane of the frame.
     * @return the main panel
     */
    protected abstract JComponent createMainPanel();

    protected abstract JToolBar createToolBar();

    protected abstract JMenuBar createMenuBar();

    /**
     * Called to allow the app to initialise the focus when the window is first displayed
     */
    protected abstract void requestFocusInWindow();


    protected abstract void loadApplicationData(String[] args);

    public JFrame getFrame() {
        return frame;
    }
}
