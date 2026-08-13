package org.colston.kicks.gui.canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CanvasResources {
    private static final Map<String, Icon> images = new HashMap<>();

    public static final int REST_STRING = 0;
    public static final int REST_PLACEMENT = 0;

    public static Icon getNoteIcon(int string, int placement) {
        String resourceName = String.format("note_%d_%d.png", string, placement);
        return getIcon(resourceName);
    }

    public static Icon getIcon(String resourceName) {
        Icon image = images.get(resourceName);
        if (image == null) {
            URL url = CanvasResources.class.getResource(resourceName);
            if (url != null) {
                try {
                    BufferedImage bi = ImageIO.read(url);
                    image = new ImageIcon(bi);
                    images.put(resourceName, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

}
