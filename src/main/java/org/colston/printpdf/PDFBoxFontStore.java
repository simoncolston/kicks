package org.colston.printpdf;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class PDFBoxFontStore {
    private static final Font DEFAULT_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);

    private final Map<String, Map<Integer, PDFont>> fonts;

    private PDFBoxFontStore(Map<String, Map<Integer, PDFont>> initial) {
        this.fonts = initial;
    }

    public static PDFBoxFontStore create() {
        Map<String, Map<Integer, PDFont>> initial = new HashMap<>();
        Map<Integer, PDFont> style;

        //Serif
        style = new HashMap<>();
        initial.put(Font.SERIF, style);
        style.put(Font.PLAIN, PDType1Font.TIMES_ROMAN);
        style.put(Font.BOLD, PDType1Font.TIMES_BOLD);
        style.put(Font.ITALIC, PDType1Font.TIMES_ITALIC);
        style.put(Font.BOLD | Font.ITALIC, PDType1Font.TIMES_BOLD_ITALIC);

        //SansSerif
        style = new HashMap<>();
        initial.put(Font.SANS_SERIF, style);
        initial.put(Font.DIALOG, style);
        initial.put(Font.DIALOG_INPUT, style);
        style.put(Font.PLAIN, PDType1Font.HELVETICA);
        style.put(Font.BOLD, PDType1Font.HELVETICA_BOLD);
        style.put(Font.ITALIC, PDType1Font.HELVETICA_OBLIQUE);
        style.put(Font.BOLD | Font.ITALIC, PDType1Font.HELVETICA_BOLD_OBLIQUE);

        //Monospaced
        style = new HashMap<>();
        initial.put(Font.MONOSPACED, style);
        style.put(Font.PLAIN, PDType1Font.COURIER);
        style.put(Font.BOLD, PDType1Font.COURIER_BOLD);
        style.put(Font.ITALIC, PDType1Font.COURIER_OBLIQUE);
        style.put(Font.BOLD | Font.ITALIC, PDType1Font.COURIER_BOLD_OBLIQUE);

        return new PDFBoxFontStore(initial);
    }

    public Font getDefaultFont() {
        return DEFAULT_FONT;
    }

    public PDFont get(Font font) {
        Map<Integer, PDFont> style = fonts.get(font.getFamily());
        if (style == null) {
            return null;
        }
        return style.get(font.getStyle());
    }

    public void put(Font font, PDFont pdfont) {
        Map<Integer, PDFont> style = fonts.computeIfAbsent(font.getFamily(), k -> new HashMap<>());
        style.put(font.getStyle(), pdfont);
    }
}
