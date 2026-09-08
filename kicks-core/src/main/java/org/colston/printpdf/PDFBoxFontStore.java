package org.colston.printpdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

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
        style.put(Font.PLAIN, new PDType1Font(FontName.TIMES_ROMAN));
        style.put(Font.BOLD, new PDType1Font(FontName.TIMES_BOLD));
        style.put(Font.ITALIC, new PDType1Font(FontName.TIMES_ITALIC));
        style.put(Font.BOLD | Font.ITALIC, new PDType1Font(FontName.TIMES_BOLD_ITALIC));

        //SansSerif
        style = new HashMap<>();
        initial.put(Font.SANS_SERIF, style);
        initial.put(Font.DIALOG, style);
        initial.put(Font.DIALOG_INPUT, style);
        style.put(Font.PLAIN, new PDType1Font(FontName.HELVETICA));
        style.put(Font.BOLD, new PDType1Font(FontName.HELVETICA_BOLD));
        style.put(Font.ITALIC, new PDType1Font(FontName.HELVETICA_OBLIQUE));
        style.put(Font.BOLD | Font.ITALIC, new PDType1Font(FontName.HELVETICA_BOLD_OBLIQUE));

        //Monospaced
        style = new HashMap<>();
        initial.put(Font.MONOSPACED, style);
        style.put(Font.PLAIN, new PDType1Font(FontName.COURIER));
        style.put(Font.BOLD, new PDType1Font(FontName.COURIER_BOLD));
        style.put(Font.ITALIC, new PDType1Font(FontName.COURIER_OBLIQUE));
        style.put(Font.BOLD | Font.ITALIC, new PDType1Font(FontName.COURIER_BOLD_OBLIQUE));

        return new PDFBoxFontStore(initial);
    }

    public Font getDefaultFont() {
        return DEFAULT_FONT;
    }

    public PDFont get(Font font) {
        Map<Integer, PDFont> style = fonts.get(font.getName());
        if (style == null) {
            return null;
        }
        return style.get(font.getStyle());
    }

    public void put(Font font, PDFont pdfont) {
        Map<Integer, PDFont> style = fonts.computeIfAbsent(font.getName(), k -> new HashMap<>());
        style.put(font.getStyle(), pdfont);
    }

    public PDFBoxFontStore loadFontMap(PDDocument doc, PDFBoxPrintFontMap fontMap) throws IOException {
        for (PDFBoxPrintFontMap.Mapping m : fontMap) {
            try (BufferedInputStream bis = new BufferedInputStream(
                    Objects.requireNonNull(m.getCls().getResourceAsStream(m.getFontResourceName())))) {
                PDFont pdfont = PDType0Font.load(doc, bis);
                put(m.getFont(), pdfont);
            }
        }
        return this;
    }
}
