package org.colston.kicks.render;

import org.colston.printpdf.PDFBoxPrintFontMap;

import java.awt.*;

public class RendererResources {
    private static final String[][] VALUES =
            {
                    {"◯", "", "", "", "", "", "", "", ""},
                    {"合", "乙", "老", "下老", "ﾛ上", "ﾛ中", "ﾛ尺", "ｲ合", "ｲ乙"},
                    {"四", "上", "中", "尺", "下尺", "ﾛ五", "ｲ老", "ｲ四", "ｲ上"},
                    {"工", "五", "六", "七", "八", "九", "ｲ尺", "ｲ工", "ｲ五"}
            };
    private static final String[][] RESOURCE_NAMES =
            {
                    {"maru", "", "", "", "", "", "", "", ""},
                    {"ai", "otsu", "rou", "gerou", "koujou", "kounaka", "koushaku", "iai", "iotsu"},
                    {"yon", "jou", "naka", "shaku", "geshaku", "kougo", "irou", "iyon", "ijou"},
                    {"kou", "go", "roku", "shichi", "hachi", "kyuu", "ishaku", "ikou", "igo"}
            };
    private static final String[] FINGER_VALUES = {"", "①", "②", "③", "④"};

    public static String getNoteText(int string, int placement) {
        return VALUES[string][placement];
    }

    public static String getNoteFingerText(int finger) {
        return FINGER_VALUES[finger];
    }

    public static String getNoteResourceName(int string, int placement) {
        return "note_" + RESOURCE_NAMES[string][placement] + ".pdf";
    }

    public static PDFBoxPrintFontMap createFontMap() {
        PDFBoxPrintFontMap fontMap = new PDFBoxPrintFontMap();
        Font font = new Font(PageRenderer.FONT_NAME, Font.PLAIN, 1);
        fontMap.add(font, PageRenderer.class, PageRenderer.FONT_RESOURCE_NAME);
        font = new Font(PageRenderer.V_FONT_NAME, Font.PLAIN, 1);
        fontMap.add(font, PageRenderer.class, PageRenderer.V_FONT_RESOURCE_NAME);
        font = new Font(PageRenderer.R_FONT_NAME, Font.PLAIN, 1);
        fontMap.add(font, PageRenderer.class, PageRenderer.R_FONT_RESOURCE_NAME);
        return fontMap;
    }
}
