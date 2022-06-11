package org.colston.kicks.gui.canvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CanvasResources {
    private static final String[][] VALUES =
            {
                    {"◯", "", "", "", "", "", "", "", ""},
                    {"合", "乙", "老", "下老", "ﾛ上", "ﾛ中", "ﾛ尺", "ｲ合", "ｲ乙"},
                    {"四", "上", "中", "尺", "下尺", "ﾛ五", "ｲ老", "ｲ四", "ｲ上"},
                    {"工", "五", "六", "七", "八", "九", "ｲ尺", "ｲ工", "ｲ五"}
            };
    private static final Map<String, Icon> images = new HashMap<>();

    public static final int REST_STRING = 0;
    public static final int REST_PLACEMENT = 0;

    public static String getNoteText(int string, int placement) {
        return VALUES[string][placement];
    }

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
